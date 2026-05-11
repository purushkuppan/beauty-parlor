export interface User {
  id: string;
  name: string;
  email: string;
  role: 'CUSTOMER' | 'STAFF' | 'ADMIN';
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}
