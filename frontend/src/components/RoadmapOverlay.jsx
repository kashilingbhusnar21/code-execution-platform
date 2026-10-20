import React, { useEffect, useState } from 'react';

/**
 * RoadmapOverlay — Full-screen animated overlay showing all phases
 * as a vertical journey timeline. Each phase node staggers in with
 * its unique gradient colour from the manifest's `color` field.
 *
 * Props:
 *   phases      – Array of phase objects from track data
 *   trackTitle  – Track title string
 *   onClose     – Callback to dismiss the overlay
 */

/* ── Gradient-colour lookup ────────────────────────────────────── */
const GRADIENT_MAP = {
  'from-amber-500 to-orange-600':   { from: '#f59e0b', to: '#ea580c', glow: 'rgba(245,158,11,0.25)' },
  'from-blue-500 to-indigo-600':    { from: '#3b82f6', to: '#4f46e5', glow: 'rgba(59,130,246,0.25)' },
  'from-emerald-500 to-teal-600':   { from: '#10b981', to: '#0d9488', glow: 'rgba(16,185,129,0.25)' },
  'from-cyan-500 to-blue-600':      { from: '#06b6d4', to: '#2563eb', glow: 'rgba(6,182,212,0.25)' },
  'from-purple-500 to-indigo-600':  { from: '#a855f7', to: '#4f46e5', glow: 'rgba(168,85,247,0.25)' },
  'from-red-500 to-rose-600':       { from: '#ef4444', to: '#e11d48', glow: 'rgba(239,68,68,0.25)' },
  'from-teal-500 to-green-600':     { from: '#14b8a6', to: '#16a34a', glow: 'rgba(20,184,166,0.25)' },
  'from-amber-500 to-yellow-600':   { from: '#f59e0b', to: '#ca8a04', glow: 'rgba(245,158,11,0.25)' },
  'from-green-500 to-emerald-600':  { from: '#22c55e', to: '#059669', glow: 'rgba(34,197,94,0.25)' },
  'from-purple-500 to-pink-600':    { from: '#a855f7', to: '#db2777', glow: 'rgba(168,85,247,0.25)' },
};

const DEFAULT_GRADIENT = { from: '#38bdf8', to: '#6366f1', glow: 'rgba(56,189,248,0.25)' };

function getGradient(colorStr) {
  return GRADIENT_MAP[colorStr] || DEFAULT_GRADIENT;
}

/* ── Phase icons (small inline SVGs per domain) ────────────────── */
function PhaseIcon({ phaseNumber }) {
  const icons = {
    1:  /* Java Foundations – code brackets */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>,
    2:  /* OOP – class cube */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 16V8a2 2 0 00-1-1.73l-7-4a2 2 0 00-2 0l-7 4A2 2 0 003 8v8a2 2 0 001 1.73l7 4a2 2 0 002 0l7-4A2 2 0 0021 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg>,
    3:  /* Core Java / Testing – beaker */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M9 3h6v5.172a2 2 0 01-.586 1.414L12 12l-2.414-2.414A2 2 0 019 8.172V3z"/><path d="M14.5 8.5L18 15a4 4 0 01-3.5 6h-5A4 4 0 016 15l3.5-6.5"/></svg>,
    4:  /* Collections – layers */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="12 2 2 7 12 12 22 7 12 2"/><polyline points="2 17 12 22 22 17"/><polyline points="2 12 12 17 22 12"/></svg>,
    5:  /* JVM – cpu */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/><line x1="9" y1="1" x2="9" y2="4"/><line x1="15" y1="1" x2="15" y2="4"/><line x1="9" y1="20" x2="9" y2="23"/><line x1="15" y1="20" x2="15" y2="23"/><line x1="20" y1="9" x2="23" y2="9"/><line x1="20" y1="14" x2="23" y2="14"/><line x1="1" y1="9" x2="4" y2="9"/><line x1="1" y1="14" x2="4" y2="14"/></svg>,
    6:  /* Multithreading – activity */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>,
    7:  /* SQL – database */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"/><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/></svg>,
    8:  /* JDBC – plug */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22v-5"/><path d="M9 8V1h6v7"/><path d="M6 11V8h12v3a6 6 0 01-12 0z"/></svg>,
    9:  /* Testing / Clean Code – check shield */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><polyline points="9 12 11 14 15 10"/></svg>,
    10: /* Spring Boot – zap */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>,
    11: /* Production / Docker – server */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="2" width="20" height="8" rx="2" ry="2"/><rect x="2" y="14" width="20" height="8" rx="2" ry="2"/><line x1="6" y1="6" x2="6.01" y2="6"/><line x1="6" y1="18" x2="6.01" y2="18"/></svg>,
    12: /* Frontend – monitor */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>,
    13: /* React – atom */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="1"/><ellipse cx="12" cy="12" rx="10" ry="4" transform="rotate(0 12 12)"/><ellipse cx="12" cy="12" rx="10" ry="4" transform="rotate(60 12 12)"/><ellipse cx="12" cy="12" rx="10" ry="4" transform="rotate(120 12 12)"/></svg>,
    14: /* Capstone – rocket */
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 00-2.91-.09z"/><path d="M12 15l-3-3a22 22 0 012-3.95A12.88 12.88 0 0122 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 01-4 2z"/><path d="M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0"/><path d="M12 15v5s3.03-.55 4-2c1.08-1.62 0-5 0-5"/></svg>,
  };
  return icons[phaseNumber] || icons[1];
}

/* ── Keyframe CSS (injected once) ──────────────────────────────── */
const STYLE_ID = 'roadmap-overlay-keyframes';
function injectKeyframes() {
  if (document.getElementById(STYLE_ID)) return;
  const style = document.createElement('style');
  style.id = STYLE_ID;
  style.textContent = `
    @keyframes roadmap-fade-in {
      from { opacity: 0; }
      to   { opacity: 1; }
    }
    @keyframes roadmap-slide-up {
      from { opacity: 0; transform: translateY(30px) scale(0.95); }
      to   { opacity: 1; transform: translateY(0) scale(1); }
    }
    @keyframes roadmap-node-enter {
      from { opacity: 0; transform: translateX(-20px) scale(0.85); }
      to   { opacity: 1; transform: translateX(0) scale(1); }
    }
    @keyframes roadmap-line-grow {
      from { transform: scaleY(0); }
      to   { transform: scaleY(1); }
    }
    @keyframes roadmap-pulse-ring {
      0%   { box-shadow: 0 0 0 0 var(--ring-color); }
      70%  { box-shadow: 0 0 0 10px transparent; }
      100% { box-shadow: 0 0 0 0 transparent; }
    }
    @keyframes roadmap-float {
      0%, 100% { transform: translateY(0); }
      50%      { transform: translateY(-6px); }
    }
    .roadmap-overlay-backdrop {
      animation: roadmap-fade-in 0.3s ease-out forwards;
    }
    .roadmap-overlay-content {
      animation: roadmap-slide-up 0.4s cubic-bezier(0.16,1,0.3,1) 0.1s forwards;
      opacity: 0;
    }
    .roadmap-node {
      opacity: 0;
      animation: roadmap-node-enter 0.5s cubic-bezier(0.16,1,0.3,1) forwards;
    }
    .roadmap-connector {
      transform-origin: top;
      transform: scaleY(0);
      animation: roadmap-line-grow 0.4s cubic-bezier(0.16,1,0.3,1) forwards;
    }
  `;
  document.head.appendChild(style);
}

/* ── Component ─────────────────────────────────────────────────── */
export default function RoadmapOverlay({ phases, trackTitle, onClose }) {
  const [visible, setVisible] = useState(false);
  const [closing, setClosing] = useState(false);

  useEffect(() => {
    injectKeyframes();
    // Prevent body scroll while overlay is open
    document.body.style.overflow = 'hidden';
    requestAnimationFrame(() => setVisible(true));
    return () => { document.body.style.overflow = ''; };
  }, []);

  const handleClose = () => {
    setClosing(true);
    setTimeout(() => onClose(), 350);
  };

  // Close on Escape key
  useEffect(() => {
    const handler = (e) => { if (e.key === 'Escape') handleClose(); };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, []);

  if (!phases || phases.length === 0) return null;

  return (
    <div
      className={`fixed inset-0 z-50 flex items-start justify-center roadmap-overlay-backdrop ${
        closing ? 'opacity-0 transition-opacity duration-300' : ''
      }`}
      style={{ backgroundColor: 'rgba(2,6,23,0.92)', backdropFilter: 'blur(12px)' }}
      onClick={(e) => { if (e.target === e.currentTarget) handleClose(); }}
    >
      {/* Ambient radial glow */}
      <div className="absolute inset-0 pointer-events-none">
        <div className="absolute top-0 left-1/4 w-96 h-96 bg-sky-500/8 rounded-full blur-3xl" />
        <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-indigo-500/8 rounded-full blur-3xl" />
      </div>

      {/* Scrollable content */}
      <div
        className={`relative w-full max-w-4xl mx-auto h-full overflow-y-auto px-4 sm:px-6 py-8 roadmap-overlay-content ${
          closing ? 'opacity-0 translate-y-8 transition-all duration-300' : ''
        }`}
        style={{ scrollbarWidth: 'thin', scrollbarColor: '#334155 transparent' }}
      >
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h2 className="text-2xl sm:text-3xl font-black text-white tracking-tight">
              Your Learning Journey
            </h2>
            <p className="mt-1 text-sm text-slate-400">
              {trackTitle} — {phases.length} Phases · {phases.reduce((sum, p) => sum + (p.topics?.length || 0), 0)} Topics
            </p>
          </div>
          <button
            onClick={handleClose}
            className="flex items-center gap-2 rounded-xl border border-slate-700 bg-slate-900/80 px-4 py-2 text-xs font-semibold text-slate-300 hover:border-sky-500/40 hover:text-white transition-all"
          >
            <span>Start Learning</span>
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        {/* ── Timeline ─────────────────────────────────────────── */}
        <div className="relative ml-6 sm:ml-10">
          {phases.map((phase, index) => {
            const grad = getGradient(phase.color);
            const topicCount = phase.topics?.length || 0;
            const delay = 0.15 + index * 0.1; // stagger
            const connectorDelay = delay + 0.15;
            const isLast = index === phases.length - 1;

            return (
              <div key={phase.id || phase.phaseNumber} className="relative">
                {/* Vertical connector line (not on last) */}
                {!isLast && (
                  <div
                    className="absolute left-[23px] top-[52px] w-[2px] roadmap-connector"
                    style={{
                      height: 'calc(100% - 24px)',
                      background: `linear-gradient(to bottom, ${grad.from}66, ${grad.from}11)`,
                      animationDelay: `${connectorDelay}s`,
                    }}
                  />
                )}

                {/* Phase node row */}
                <div
                  className="roadmap-node flex items-start gap-4 sm:gap-5 pb-10 group cursor-default"
                  style={{ animationDelay: `${delay}s` }}
                >
                  {/* Circle node */}
                  <div className="relative shrink-0">
                    <div
                      className="flex h-12 w-12 items-center justify-center rounded-2xl border-2 text-white shadow-lg"
                      style={{
                        borderColor: grad.from,
                        background: `linear-gradient(135deg, ${grad.from}22, ${grad.to}22)`,
                        boxShadow: `0 0 20px ${grad.glow}, 0 4px 12px rgba(0,0,0,0.3)`,
                        '--ring-color': grad.glow,
                      }}
                    >
                      <div className="h-5 w-5" style={{ color: grad.from }}>
                        <PhaseIcon phaseNumber={phase.phaseNumber} />
                      </div>
                    </div>
                    {/* Phase number badge */}
                    <div
                      className="absolute -top-1.5 -right-1.5 flex h-5 w-5 items-center justify-center rounded-full text-[9px] font-black text-white shadow-md"
                      style={{ background: `linear-gradient(135deg, ${grad.from}, ${grad.to})` }}
                    >
                      {phase.phaseNumber}
                    </div>
                  </div>

                  {/* Phase info card */}
                  <div
                    className="flex-1 rounded-2xl border border-slate-800/80 bg-slate-900/50 p-4 sm:p-5 backdrop-blur-sm transition-all duration-300 group-hover:border-slate-700 group-hover:bg-slate-900/70"
                    style={{
                      borderLeftWidth: '3px',
                      borderLeftColor: grad.from,
                    }}
                  >
                    <div className="flex flex-wrap items-center gap-2 mb-1">
                      <span
                        className="text-[10px] uppercase font-bold tracking-widest"
                        style={{ color: grad.from }}
                      >
                        Phase {phase.phaseNumber}
                      </span>
                      <span className="text-slate-700">·</span>
                      <span className="text-[11px] text-slate-500 font-medium">
                        ~{phase.estimatedHours}h total effort
                      </span>
                    </div>
                    <h3 className="text-base sm:text-lg font-bold text-white leading-snug">
                      {phase.title}
                    </h3>
                    <div className="mt-2 flex flex-wrap items-center gap-3">
                      <span className="inline-flex items-center gap-1.5 rounded-lg bg-slate-800/80 px-2.5 py-1 text-[11px] font-semibold text-slate-300">
                        <svg className="h-3 w-3 text-slate-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <path d="M4 19.5A2.5 2.5 0 016.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/>
                        </svg>
                        {topicCount} {topicCount === 1 ? 'lesson' : 'lessons'}
                      </span>
                      {phase.topics?.some(t => t.hasProject) && (
                        <span className="inline-flex items-center gap-1 rounded-lg border border-amber-500/30 bg-amber-500/10 px-2.5 py-1 text-[10px] font-bold text-amber-300">
                          <svg className="h-3 w-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                          </svg>
                          PROJECT
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}

          {/* Finish line */}
          <div
            className="roadmap-node flex items-center gap-4 sm:gap-5 pb-4"
            style={{ animationDelay: `${0.15 + phases.length * 0.1}s` }}
          >
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl border-2 border-emerald-500 bg-emerald-500/15 text-emerald-400 shadow-lg shadow-emerald-500/20">
              <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="M22 11.08V12a10 10 0 11-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
            </div>
            <div>
              <h3 className="text-lg font-bold text-emerald-300">
                Job-Ready Developer 🎉
              </h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Complete all phases to achieve junior developer readiness
              </p>
            </div>
          </div>
        </div>

        {/* Bottom CTA */}
        <div className="mt-8 mb-4 flex justify-center">
          <button
            onClick={handleClose}
            className="flex items-center gap-2 rounded-2xl border border-sky-500/40 bg-gradient-to-r from-sky-500 to-indigo-600 px-8 py-3 text-sm font-bold uppercase tracking-wider text-white shadow-lg shadow-sky-500/20 hover:from-sky-400 hover:to-indigo-500 transition-all"
          >
            <span>Begin Your Journey</span>
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </button>
        </div>
      </div>
    </div>
  );
}
