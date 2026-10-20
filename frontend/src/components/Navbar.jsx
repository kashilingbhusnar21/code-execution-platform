import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { api } from '../services/api';

export default function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const isAuth = api.isAuthenticated();
  const user = api.getUser();

  const handleLogout = () => {
    api.logout();
    navigate('/login');
  };

  const navLinks = [
    {
      name: 'Career Roadmaps',
      path: '/learning',
      badge: 'PRO',
      active: location.pathname.startsWith('/learning'),
      icon: (
        <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
          <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
        </svg>
      ),
    },
    {
      name: 'DSA Library',
      path: '/problems',
      badge: null,
      active: location.pathname.startsWith('/problems'),
      icon: (
        <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <polyline points="16 18 22 12 16 6" />
          <polyline points="8 6 2 12 8 18" />
        </svg>
      ),
    },
    {
      name: 'Code Editor',
      path: '/editor',
      badge: null,
      active: location.pathname === '/editor',
      icon: (
        <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <rect x="2" y="3" width="20" height="14" rx="2" ry="2" />
          <line x1="8" y1="21" x2="16" y2="21" />
          <line x1="12" y1="17" x2="12" y2="21" />
        </svg>
      ),
    },
  ];

  return (
    <header className="relative z-30 border-b border-slate-800/80 bg-slate-950/80 backdrop-blur-xl">
      <div className="mx-auto flex h-16 w-full max-w-7xl items-center justify-between gap-3 px-4 sm:px-6 lg:px-8">
        {/* Brand / Logo */}
        <div className="flex items-center gap-6">
          <Link to="/learning" className="flex items-center gap-3 group">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-sky-500/20 bg-sky-500/10 text-sky-400 shadow-lg shadow-sky-500/10 group-hover:border-sky-400/50 transition-all">
              <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M12 2L2 7l10 5 10-5-10-5z" />
                <path d="M2 17l10 5 10-5" />
                <path d="M2 12l10 5 10-5" />
              </svg>
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-lg font-bold tracking-tight text-white group-hover:text-sky-300 transition-colors">
                  CodeArena
                </span>
                <span className="rounded-full border border-sky-500/30 bg-sky-500/15 px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wider text-sky-300">
                  Career Suite
                </span>
              </div>
              <p className="truncate text-xs font-medium text-cyan-400/90 tracking-wide">
                Built by Kashiling Bhusnar
              </p>
            </div>
          </Link>

          {/* Nav Links */}
          <nav className="hidden md:flex items-center gap-1.5 border-l border-slate-800 pl-6">
            {navLinks.map((link) => (
              <Link
                key={link.name}
                to={link.path}
                className={`flex items-center gap-2 rounded-xl px-3 py-1.5 text-xs font-semibold transition-all ${
                  link.active
                    ? 'border border-sky-500/40 bg-sky-500/20 text-white shadow-md shadow-sky-500/10 ring-1 ring-sky-400/30'
                    : 'text-slate-400 hover:bg-slate-900/60 hover:text-slate-200'
                }`}
              >
                {link.icon}
                <span>{link.name}</span>
                {link.badge && (
                  <span className="rounded bg-sky-400/20 px-1.5 py-0.2 text-[9px] font-bold text-sky-300">
                    {link.badge}
                  </span>
                )}
              </Link>
            ))}
          </nav>
        </div>

        {/* User Auth Info & Logout / Login */}
        <div className="flex items-center gap-3">
          {isAuth ? (
            <div className="flex items-center gap-3">
              <div className="hidden sm:flex flex-col text-right">
                <span className="text-xs font-semibold text-slate-200">{user?.username || 'Developer'}</span>
                <span className="text-[10px] text-slate-400 truncate max-w-[150px]">{user?.email}</span>
              </div>
              <button
                onClick={handleLogout}
                className="rounded-xl border border-slate-700 bg-slate-900/80 px-3 py-1.5 text-xs font-medium text-slate-300 hover:border-rose-500/40 hover:bg-rose-500/10 hover:text-rose-300 transition-all"
              >
                Sign Out
              </button>
            </div>
          ) : (
            <Link
              to="/login"
              className="rounded-xl border border-sky-500/30 bg-sky-500/15 px-3.5 py-1.5 text-xs font-semibold text-sky-200 shadow-sm hover:border-sky-400/50 hover:bg-sky-500/25 transition-all"
            >
              Sign In
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}
