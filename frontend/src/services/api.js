import axios from 'axios';

// Uses Vite proxy in vite.config.js → http://localhost:8080
const API_BASE_URL = '/api';

export const api = {
  runCode: async (code, language, input, userId) => {
    const response = await axios.post(`${API_BASE_URL}/code/run`, {
      code,
      language,
      input: input || '',
      userId,
    });
    return response.data;
  },

  getHistory: async (userId) => {
    const response = await axios.get(
      `${API_BASE_URL}/code/history/${userId}`
    );
    return response.data;
  },

  getCodeById: async (id) => {
    const response = await axios.get(`${API_BASE_URL}/code/${id}`);
    return response.data;
  },
};
