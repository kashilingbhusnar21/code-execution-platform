import React, { useState } from 'react';
import { api } from '../services/api';

const AuthModal = ({ onClose, onAuthSuccess }) => {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      let response;
      if (isLogin) {
        response = await api.login(email, password);
      } else {
        response = await api.register(username, email, password);
      }

      api.saveAuth(response.token, {
        userId: response.userId,
        username: response.username,
        email: response.email,
      });

      onAuthSuccess();
      onClose();
    } catch (err) {
      setError(err.response?.data || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h2>{isLogin ? 'Login' : 'Register'}</h2>
          <button type="button" onClick={onClose} className="modal-close">
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {!isLogin && (
            <div className="form-group">
              <label htmlFor="username">Username</label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
          )}

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {error && <div className="error">{error}</div>}

          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? 'Processing...' : isLogin ? 'Login' : 'Register'}
          </button>

          <div className="auth-switch">
            {isLogin ? "Don't have an account? " : 'Already have an account? '}
            <button
              type="button"
              onClick={() => {
                setIsLogin(!isLogin);
                setError('');
              }}
              className="btn-link"
            >
              {isLogin ? 'Register' : 'Login'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AuthModal;
