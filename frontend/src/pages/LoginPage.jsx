import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';

const LoginPage = ({ onAuthSuccess }) => {
  const navigate = useNavigate();
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (api.isAuthenticated()) {
      navigate('/editor', { replace: true });
    }
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = isLogin
        ? await api.login(email, password)
        : await api.register(username, email, password);

      api.saveAuth(response.token, {
        userId: response.userId,
        username: response.username,
        email: response.email,
      });

      if (typeof onAuthSuccess === 'function') {
        onAuthSuccess();
      }
      navigate('/editor', { replace: true });
    } catch (err) {
      setError(err.response?.data || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="relative min-h-screen overflow-hidden bg-[#0b1120] text-slate-100">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(251,146,60,0.10),transparent_26%),radial-gradient(circle_at_top_right,rgba(245,158,11,0.10),transparent_24%),linear-gradient(to_bottom,rgba(15,23,42,0.94),rgba(2,6,23,1))]" />

      <div className="relative flex min-h-screen items-center justify-center px-4 py-8">
        <div className="w-full max-w-md rounded-[28px] border border-slate-800 bg-[#1b1b2b]/90 p-6 shadow-2xl shadow-black/30 backdrop-blur-xl sm:p-8">
          <div className="mb-8">
            <div className="flex items-center justify-center gap-3">
              <div className="flex h-14 w-14 items-center justify-center rounded-2xl border border-orange-500/25 bg-orange-500/10 text-orange-400 shadow-lg shadow-orange-500/10">
                <svg className="h-7 w-7" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
                  <path d="M12 3l7.5 4.3v8.4L12 20l-7.5-4.3V7.3L12 3z" strokeLinecap="round" strokeLinejoin="round" />
                  <path d="M12 7l3.5 2v4L12 15l-3.5-2V9L12 7z" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              </div>
              <div>
                <h1 className="text-[28px] font-semibold tracking-tight text-orange-400">CodeArena</h1>
              </div>
            </div>

            <div className="mt-8 text-center">
              <p className="text-[18px] font-medium text-slate-300">
                {isLogin ? 'Welcome back! Please login to continue.' : 'Create your account to start coding.'}
              </p>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            {!isLogin && (
              <div>
                <label htmlFor="username" className="mb-2 block text-sm font-semibold text-slate-200">
                  Username
                </label>
                <input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Enter your username"
                  required
                  className="w-full rounded-xl border border-slate-700 bg-[#11131f] px-4 py-3 text-slate-100 outline-none transition-all duration-200 placeholder:text-slate-500 focus:border-orange-500 focus:ring-1 focus:ring-orange-500/30"
                />
              </div>
            )}

            <div>
              <label htmlFor="email" className="mb-2 block text-sm font-semibold text-slate-200">
                Email
              </label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter your email"
                required
                className="w-full rounded-xl border border-slate-700 bg-[#11131f] px-4 py-3 text-slate-100 outline-none transition-all duration-200 placeholder:text-slate-500 focus:border-orange-500 focus:ring-1 focus:ring-orange-500/30"
              />
            </div>

            <div>
              <label htmlFor="password" className="mb-2 block text-sm font-semibold text-slate-200">
                Password
              </label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                required
                className="w-full rounded-xl border border-slate-700 bg-[#11131f] px-4 py-3 text-slate-100 outline-none transition-all duration-200 placeholder:text-slate-500 focus:border-orange-500 focus:ring-1 focus:ring-orange-500/30"
              />
            </div>

            {error && (
              <div className="rounded-xl border border-rose-500/20 bg-rose-500/10 px-4 py-3 text-sm text-rose-300">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="flex w-full items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-[#ffad33] to-[#ff7433] px-4 py-3 font-semibold text-slate-950 shadow-lg shadow-orange-500/20 transition-all duration-200 hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? (
                <>
                  <svg className="h-5 w-5 animate-spin" viewBox="0 0 24 24" fill="none">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-90" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Processing
                </>
              ) : (
                <>{isLogin ? 'Sign In' : 'Sign Up'}</>
              )}
            </button>

            <div className="pt-2 text-center text-sm text-slate-400">
              {isLogin ? "Don't have an account? " : 'Already have an account? '}
              <button
                type="button"
                onClick={() => {
                  setIsLogin(!isLogin);
                  setError('');
                }}
                className="font-semibold text-orange-400 transition-colors hover:text-orange-300"
              >
                {isLogin ? 'Create one' : 'Sign in'}
              </button>
            </div>

            <div className="pt-4">
              <div className="h-px bg-slate-700/70" />
              <p className="mt-4 text-center text-xs leading-5 text-slate-500">
                By continuing, you agree to our Terms of Service and Privacy Policy
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
