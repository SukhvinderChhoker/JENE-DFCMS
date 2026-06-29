export interface User {
  id: number;
  username: string;
  forename: string;
  surname: string;
  middleName?: string;
  email: string;
  telephone?: string;
  altTelephone?: string;
  fax?: string;
  jobTitle?: string;
  photo?: string;
  active: boolean;
  team?: string;
  department?: string;
  roles: string[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  roles: string[];
  userId: number;
}
