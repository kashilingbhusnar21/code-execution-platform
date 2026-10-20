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

  getUserInfo: async () => {
    const response = await axiosWithAuth.get(`${AUTH_BASE_URL}/me`);
    return response.data;
  },

  // Code execution endpoints (protected)
  runCode: async (code, language, input, problemId) => {
    const payload = {
      code,
      language,
      input: input || '',
    };
    if (problemId) {
      payload.problemId = problemId;
    }
    const response = await axiosWithAuth.post(`${API_BASE_URL}/code/run`, payload);
    return response.data;
  },

  submitCode: async (code, language, problemId) => {
    const response = await axiosWithAuth.post(`${API_BASE_URL}/code/submit`, {
      code,
      language,
      problemId,
    });
    return response.data;
  },

  getHistory: async (problemId) => {
    const url = problemId ? `${API_BASE_URL}/code/history?problemId=${problemId}` : `${API_BASE_URL}/code/history`;
    const response = await axiosWithAuth.get(url);
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

  // Problem endpoints
  getAllProblems: async () => {
    const response = await axios.get(`${API_BASE_URL}/problems`);
    return response.data;
  },

  getProblemById: async (id) => {
    const response = await axios.get(`${API_BASE_URL}/problems/${id}`);
    return response.data;
  },

  getProblemsByDifficulty: async (difficulty) => {
    const response = await axios.get(`${API_BASE_URL}/problems/difficulty/${difficulty}`);
    return response.data;
  },

  getProblemsByTopic: async (topicName) => {
    const response = await axios.get(`${API_BASE_URL}/problems/topic/${topicName}`);
    return response.data;
  },

  searchProblems: async (keyword) => {
    const response = await axios.get(`${API_BASE_URL}/problems/search`, {
      params: { keyword }
    });
    return response.data;
  },

  filterProblems: async (difficulty, topicId) => {
    const response = await axios.get(`${API_BASE_URL}/problems/filter`, {
      params: { difficulty, topicId }
    });
    return response.data;
  },

  getAllTopics: async () => {
    const response = await axios.get(`${API_BASE_URL}/problems/topics/all`);
    return response.data;
  },

  getUserProgress: async () => {
    const response = await axiosWithAuth.get(`${API_BASE_URL}/problems/progress`);
    return response.data;
  },

  // Learning Platform Endpoints
  getLearningTracks: async () => {
    const response = await axiosWithAuth.get(`${API_BASE_URL}/learning/tracks`);
    return response.data;
  },

  getTrackRoadmap: async (trackSlug) => {
    const response = await axiosWithAuth.get(`${API_BASE_URL}/learning/tracks/${trackSlug}`);
    return response.data;
  },

  getTopicDetails: async (topicSlug) => {
    const response = await axiosWithAuth.get(`${API_BASE_URL}/learning/topics/${topicSlug}`);
    return response.data;
  },

  getUserTrackProgress: async (trackSlug) => {
    const response = await axiosWithAuth.get(`${API_BASE_URL}/learning/progress/${trackSlug}`);
    return response.data;
  },

  updateTopicProgress: async (topicSlug, status = 'COMPLETED', quizScore = null) => {
    const response = await axiosWithAuth.post(`${API_BASE_URL}/learning/topics/${topicSlug}/progress`, {
      status,
      quizScore,
    });
    return response.data;
  },
};