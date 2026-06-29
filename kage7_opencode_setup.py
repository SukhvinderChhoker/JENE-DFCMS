"""
Kage7 OpenCode Universal Auto-Setup Script
============================================
Automatically configures OpenCode to connect to ANY Kage7 gateway.
Fetches available models dynamically from the gateway — no hardcoding needed.

Usage:
    python kage7_opencode_setup.py
    python kage7_opencode_setup.py --url https://your-gateway.up.railway.app --key kage7-sk-xxxxx
"""

import argparse
import json
import os
import platform
import subprocess
import sys
import urllib.request
import urllib.error


# ──────────────────────────────────────────────
# CONSTANTS
# ──────────────────────────────────────────────

PROVIDER_NAME = "kage7"
PROVIDER_DISPLAY = "Kage7"
DEFAULT_CONTEXT = 200000
DEFAULT_OUTPUT = 65536


# ──────────────────────────────────────────────
# HELPERS
# ──────────────────────────────────────────────

def print_banner():
    print()
    print("=" * 55)
    print("   Kage7 - Universal OpenCode Setup")
    print("=" * 55)
    print()


def print_step(num, total, msg):
    print(f"  [{num}/{total}] {msg}")


def print_ok(msg):
    print(f"   [OK]  {msg}")


def print_warn(msg):
    print(f"   [WARN]  {msg}")


def print_fail(msg):
    print(f"   [FAIL]  {msg}")


def normalize_url(url):
    """Normalize the gateway URL — strip trailing slashes and /v1 suffix."""
    url = url.strip().rstrip("/")
    if url.endswith("/v1"):
        url = url[:-3]
    return url


# ──────────────────────────────────────────────
# FETCH MODELS FROM GATEWAY
# ──────────────────────────────────────────────

def fetch_models(base_url, api_key):
    """Hit the /v1/models endpoint and return a dict of model configs."""
    models_url = f"{base_url}/v1/models"

    req = urllib.request.Request(
        models_url,
        headers={
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json",
        },
    )

    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            body = json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        error_body = ""
        try:
            error_body = e.read().decode("utf-8", errors="replace")
        except Exception:
            pass
        print_fail(f"Gateway returned HTTP {e.code}: {error_body[:200]}")
        return None
    except urllib.error.URLError as e:
        print_fail(f"Could not reach gateway: {e.reason}")
        return None
    except Exception as e:
        print_fail(f"Unexpected error fetching models: {e}")
        return None

    # Parse OpenAI-compatible /v1/models response
    raw_models = body.get("data", body.get("models", []))
    if isinstance(body, list):
        raw_models = body

    if not raw_models:
        print_fail("Gateway returned no models. Check your API key permissions.")
        return None

    models = {}
    for m in raw_models:
        if isinstance(m, dict):
            model_id = m.get("id", m.get("model", ""))
        elif isinstance(m, str):
            model_id = m
        else:
            continue

        if not model_id:
            continue

        # Use context/output limits from the response if available, else defaults
        context = DEFAULT_CONTEXT
        output = DEFAULT_OUTPUT

        if isinstance(m, dict):
            # Some gateways expose these
            context = m.get("context_length", m.get("max_context", DEFAULT_CONTEXT))
            output = m.get("max_output", m.get("max_output_tokens", DEFAULT_OUTPUT))

        # Build a human-friendly display name
        display = model_id.replace("-", " ").replace("_", " ").title()

        models[model_id] = {
            "name": display,
            "limit": {
                "context": context,
                "output": output,
            },
        }

    return models


# ──────────────────────────────────────────────
# OS PATHS
# ──────────────────────────────────────────────

def get_paths():
    """Get correct config/data paths based on OS."""
    system = platform.system()
    home = os.path.expanduser("~")

    if system == "Windows":
        home = os.environ.get("USERPROFILE", home)

    if system == "Linux":
        config_dir = os.environ.get("XDG_CONFIG_HOME", os.path.join(home, ".config"))
        data_dir = os.environ.get("XDG_DATA_HOME", os.path.join(home, ".local", "share"))
    else:
        config_dir = os.path.join(home, ".config")
        data_dir = os.path.join(home, ".local", "share")

    config_dir = os.path.join(config_dir, "opencode")
    data_dir = os.path.join(data_dir, "opencode")

    return {
        "config_dir": config_dir,
        "data_dir": data_dir,
        "config_file": os.path.join(config_dir, "opencode.jsonc"),
        "auth_file": os.path.join(data_dir, "auth.json"),
    }


# ──────────────────────────────────────────────
# SETUP STEPS
# ──────────────────────────────────────────────

def load_jsonc(filepath):
    """Load a JSONC file (strips // comments)."""
    if not os.path.exists(filepath):
        return {}
    try:
        with open(filepath, "r", encoding="utf-8") as f:
            lines = [
                line for line in f.read().splitlines()
                if not line.lstrip().startswith("//")
            ]
        return json.loads("\n".join(lines))
    except (json.JSONDecodeError, Exception):
        return {}


def save_json(filepath, data):
    """Write JSON with trailing newline."""
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    with open(filepath, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write("\n")


def setup_config(paths, base_url, models):
    """Create/update opencode.jsonc with the Kage7 provider + discovered models."""
    config = load_jsonc(paths["config_file"])

    provider_entry = {
        "npm": "@ai-sdk/openai-compatible",
        "name": PROVIDER_DISPLAY,
        "options": {
            "baseURL": f"{base_url}/v1",
        },
        "models": models,
    }

    config.setdefault("$schema", "https://opencode.ai/config.json")
    config.setdefault("provider", {})
    config["provider"][PROVIDER_NAME] = provider_entry

    save_json(paths["config_file"], config)
    print_ok(f"Config saved -> {paths['config_file']}")


def setup_auth(paths, api_key):
    """Add/update API key in auth.json."""
    auth = {}
    if os.path.exists(paths["auth_file"]):
        try:
            with open(paths["auth_file"], "r", encoding="utf-8") as f:
                auth = json.load(f)
        except Exception:
            auth = {}

    auth[PROVIDER_NAME] = {"type": "api", "key": api_key}

    save_json(paths["auth_file"], auth)
    print_ok(f"API key saved -> {paths['auth_file']}")


def install_adapter(paths):
    """Install @ai-sdk/openai-compatible via npm."""
    config_dir = paths["config_dir"]
    pkg_file = os.path.join(config_dir, "package.json")

    if not os.path.exists(pkg_file):
        save_json(pkg_file, {"dependencies": {}})

    try:
        result = subprocess.run(
            ["npm", "install", "@ai-sdk/openai-compatible"],
            cwd=config_dir,
            capture_output=True,
            text=True,
            timeout=60,
            shell=(platform.system() == "Windows"),
        )
        if result.returncode == 0:
            print_ok("Installed @ai-sdk/openai-compatible")
            return True
        else:
            print_warn(f"npm install failed: {result.stderr.strip()[:200]}")
            return False
    except FileNotFoundError:
        print_warn("npm not found — install Node.js, then run:")
        print(f"         cd {config_dir} && npm install @ai-sdk/openai-compatible")
        return False
    except subprocess.TimeoutExpired:
        print_warn("npm install timed out — check your internet")
        return False


def verify_setup():
    """Quick check if OpenCode can see the provider."""
    try:
        result = subprocess.run(
            ["opencode", "models", PROVIDER_NAME],
            capture_output=True,
            text=True,
            timeout=15,
            shell=(platform.system() == "Windows"),
        )
        if result.returncode == 0 and result.stdout.strip():
            models = result.stdout.strip().split("\n")
            print_ok(f"OpenCode sees {len(models)} model(s):")
            for m in models:
                print(f"         - {m.strip()}")
            return True
        else:
            print_warn("OpenCode couldn't list models (is it installed?)")
            return False
    except FileNotFoundError:
        print_warn("'opencode' not found — install: npm install -g opencode-ai")
        return False
    except Exception:
        print_warn("Could not verify — but config files are ready")
        return False


# ──────────────────────────────────────────────
# MAIN
# ──────────────────────────────────────────────

def main():
    parser = argparse.ArgumentParser(
        description="   Kage7 - Universal OpenCode Setup",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument("--url", help="Kage7 gateway URL")
    parser.add_argument("--key", help="Kage7 API key")
    args = parser.parse_args()

    print_banner()

    # ── Collect gateway URL ──
    gateway_url = args.url
    if not gateway_url:
        gateway_url = input("  Gateway URL: ").strip()
    if not gateway_url:
        print_fail("No URL provided. Exiting.")
        sys.exit(1)
    gateway_url = normalize_url(gateway_url)
    print_ok(f"Gateway: {gateway_url}")
    print()

    # ── Collect API key ──
    api_key = args.key
    if not api_key:
        api_key = input("  API Key: ").strip()
    if not api_key:
        print_fail("No API key provided. Exiting.")
        sys.exit(1)
    print_ok(f"Key:     {api_key[:16]}...{'*' * 8}")
    print()

    # ── Step 1: Fetch models ──
    print_step(1, 5, "Fetching models from gateway...")
    models = fetch_models(gateway_url, api_key)
    if not models:
        print_fail("Cannot continue without models. Check URL & key.")
        sys.exit(1)
    print_ok(f"Found {len(models)} model(s):")
    for mid, minfo in models.items():
        print(f"         - {mid}  ({minfo['name']})")
    print()

    # ── Step 2: Config ──
    paths = get_paths()
    print_step(2, 5, "Writing provider config...")
    setup_config(paths, gateway_url, models)

    # ── Step 3: Auth ──
    print_step(3, 5, "Saving API key...")
    setup_auth(paths, api_key)

    # ── Step 4: npm adapter ──
    print_step(4, 5, "Installing npm adapter...")
    install_adapter(paths)

    # ── Step 5: Verify ──
    print_step(5, 5, "Verifying setup...")
    verify_setup()

    # ── Done ──
    print()
    print("=" * 55)
    print("   Setup Complete!")
    print("=" * 55)
    print()
    print("  Registered models:")
    for mid in models:
        print(f"    - {PROVIDER_NAME}/{mid}")
    print()
    first_model = list(models.keys())[0]
    print("  Quick start:")
    print(f"    opencode run -m {PROVIDER_NAME}/{first_model} \"your prompt\"")
    print()
    print("  Restart OpenCode if it's already running.")
    print()


if __name__ == "__main__":
    main()
