import axios from 'axios';

// Uses Vite proxy in vite.config.js → http://localhost:8080
const API_BASE_URL = '/api';
const AUTH_BASE_URL = '/auth';

// Get JWT token from localStorage
const getToken = () => localStorage.getItem('token');

// Create axios instance with auth header
const axiosWithAuth = axios.create();

axiosWithAuth.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const api = {
  // Auth endpoints
  register: async (username, email, password) => {
    const response = await axios.post(`${AUTH_BASE_URL}/register`, {
      username,
      email,
      password,
    });
    return response.data;
  },

  login: async (email, password) => {
    const response = await axios.post(`${AUTH_BASE_URL}/login`, {
      email,
      password,
    });
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  saveAuth: (token, user) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
  },

  getUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  isAuthenticated: () => !!getToken(),

  // Code execution endpoints (protected)
  runCode: async (code, language, input) => {
    const response = await axiosWithAuth.post(`${API_BASE_URL}/code/run`, {
      code,
      language,
      input: input || '',
    });
    return response.data;
  },

  getHistory: async () => {
    const response = await axiosWithAuth.get(`${API_BASE_URL}/code/history`);
    return response.data;
  },

  getCodeById: async (id) => {
    const response = await axiosWithAuth.get(`${API_BASE_URL}/code/${id}`);
    return response.data;
  },

  getSubmissionStatus: async (id) => {
    const response = await axiosWithAuth.get(`${API_BASE_URL}/code/status/${id}`);
    return response.data;
  },
};
