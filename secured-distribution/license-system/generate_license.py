#!/usr/bin/env python3
"""
JENE-DFCMS License Generator
RSA-2048 signed license generation for offline validation.

Usage:
    python generate_license.py --customer "Customer Name" --type trial --days 20
    python generate_license.py --customer "Customer Name" --type annual --days 365
    python generate_license.py --customer "Customer Name" --type annual --expiry 2027-06-29
"""

import argparse
import json
import uuid
import hashlib
import platform
import os
import sys
from datetime import datetime, timedelta

try:
    from cryptography.hazmat.primitives import hashes, serialization
    from cryptography.hazmat.primitives.asymmetric import padding
    from cryptography.hazmat.backends import default_backend
except ImportError:
    print("ERROR: 'cryptography' library required. Install with: pip install cryptography")
    sys.exit(1)


LICENSE_VERSION = "1.0"
APP_NAME = "JENE-DFCMS"


def get_hardware_id():
    """Generate a deterministic hardware fingerprint."""
    raw = f"{platform.node()}-{platform.machine()}-{platform.processor()}"
    return hashlib.sha256(raw.encode()).hexdigest()[:16]


def load_private_key(key_path):
    """Load RSA private key from PEM file."""
    with open(key_path, "rb") as f:
        return serialization.load_pem_private_key(f.read(), password=None, backend=default_backend())


def sign_license(license_data: dict, private_key) -> str:
    """Sign license data with RSA private key, return base64 signature."""
    import base64
    payload = json.dumps(license_data, sort_keys=True, separators=(",", ":")).encode()
    signature = private_key.sign(
        payload,
        padding.PKCS1v15(),
        hashes.SHA256()
    )
    return base64.b64encode(signature).decode()


def generate_license(customer_name, license_type, days=None, expiry_date=None, key_path=None, output_dir=None):
    """Generate a signed license file."""
    if key_path is None:
        key_path = os.path.join(os.path.dirname(__file__), "keys", "private_key.pem")
    if output_dir is None:
        output_dir = os.path.join(os.path.dirname(__file__), "licenses")

    private_key = load_private_key(key_path)

    now = datetime.utcnow()
    if expiry_date:
        expires = datetime.strptime(expiry_date, "%Y-%m-%d")
    elif days:
        expires = now + timedelta(days=days)
    else:
        raise ValueError("Must specify either --days or --expiry")

    if license_type == "trial":
        days_valid = (expires - now).days
        if days_valid > 30:
            print(f"WARNING: Trial license limited to 30 days. Clamping to 30 days.")
            expires = now + timedelta(days=30)
    elif license_type == "annual":
        days_valid = (expires - now).days
        if days_valid > 400:
            print(f"WARNING: Annual license capped at 400 days. Clamping to 365 days.")
            expires = now + timedelta(days=365)

    license_id = str(uuid.uuid4()).upper()
    license_data = {
        "licenseId": license_id,
        "appName": APP_NAME,
        "version": LICENSE_VERSION,
        "customerName": customer_name,
        "licenseType": license_type,
        "issueDate": now.strftime("%Y-%m-%dT%H:%M:%SZ"),
        "expiryDate": expires.strftime("%Y-%m-%dT%H:%M:%SZ"),
        "hardwareId": get_hardware_id(),
        "maxUsers": 50 if license_type == "annual" else 5,
        "features": ["cases", "evidence", "tasks", "reports", "quiz"],
    }

    signature = sign_license(license_data, private_key)
    license_file = {
        "license": license_data,
        "signature": signature,
    }

    os.makedirs(output_dir, exist_ok=True)
    filename = f"jene-dfcms-{license_type}-{license_id[:8]}.license"
    filepath = os.path.join(output_dir, filename)
    with open(filepath, "w") as f:
        json.dump(license_file, f, indent=2)

    print(f"\n{'='*60}")
    print(f"  License Generated Successfully")
    print(f"{'='*60}")
    print(f"  ID:            {license_id}")
    print(f"  Customer:      {customer_name}")
    print(f"  Type:          {license_type.upper()}")
    print(f"  Issue Date:    {now.strftime('%Y-%m-%d')}")
    print(f"  Expiry Date:   {expires.strftime('%Y-%m-%d')}")
    print(f"  Days Valid:    {(expires - now).days}")
    print(f"  Hardware ID:   {get_hardware_id()}")
    print(f"  Max Users:     {license_data['maxUsers']}")
    print(f"  File:          {filepath}")
    print(f"{'='*60}\n")

    return filepath


def main():
    parser = argparse.ArgumentParser(description="JENE-DFCMS License Generator")
    parser.add_argument("--customer", required=True, help="Customer/company name")
    parser.add_argument("--type", choices=["trial", "annual"], required=True, help="License type")
    parser.add_argument("--days", type=int, help="License duration in days")
    parser.add_argument("--expiry", help="Expiry date (YYYY-MM-DD)")
    parser.add_argument("--key", help="Path to private key PEM")
    parser.add_argument("--output", help="Output directory for license file")
    args = parser.parse_args()

    if not args.days and not args.expiry:
        if args.type == "trial":
            args.days = 20
            print(f"Trial license: defaulting to {args.days} days")
        else:
            args.days = 365
            print(f"Annual license: defaulting to {args.days} days")

    generate_license(args.customer, args.type, args.days, args.expiry, args.key, args.output)


if __name__ == "__main__":
    main()
