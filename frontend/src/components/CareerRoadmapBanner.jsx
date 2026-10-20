import React, { useState, memo } from 'react';
import { Link } from 'react-router-dom';

/**
 * CareerRoadmapBanner - High-performance, interactive Career Roadmaps showcase
 * placed inside the DSA Problem Library page (/problems).
 * 
 * Performance:
 * - Memoized with React.memo to prevent re-renders when parent filters/searches problems.
 * - Hardware-accelerated CSS animations and transitions.
 * - Zero network requests (uses pre-configured metadata).
 * - Persistent collapse/expand state via localStorage.
 */
const TRACKS_PREVIEW = [
  {
    slug: 'java-fullstack',
    title: 'Java Full Stack Developer',
    badge: 'Comprehensive Path',
    badgeColor: 'border-sky-500/40 bg-sky-500/15 text-sky-300',
    phases: 14,
    topics: 98,
    duration: '32 Weeks',
    accentFrom: '#38bdf8',
    accentTo: '#6366f1',
    borderGlow: 'hover:border-sky-500/50 hover:shadow-sky-500/15',
    summary: 'Master Java 17/21, OOP, Spring Boot 3 microservices, Docker, React 18, and full-stack integration.',
    skills: ['Java Core', 'OOP', 'Spring Boot 3', 'React 18', 'Docker', 'RabbitMQ'],
  },
  {
    slug: 'java-backend',
    title: 'Java Backend Developer',
    badge: 'Backend Specialist',
    badgeColor: 'border-amber-500/40 bg-amber-500/15 text-amber-300',
    phases: 12,
    topics: 40,
    duration: '24 Weeks',
    accentFrom: '#f59e0b',
    accentTo: '#ea580c',
    borderGlow: 'hover:border-amber-500/50 hover:shadow-amber-500/15',
    summary: 'Deep dive into Java fundamentals, JVM memory, multithreading, SQL, JDBC, Spring Boot, and cloud architecture.',
    skills: ['JVM Internals', 'Concurrency', 'SQL & JDBC', 'Spring Boot', 'REST APIs', 'Security'],
  },
];

const CareerRoadmapBanner = memo(function CareerRoadmapBanner() {
  const [isCollapsed, setIsCollapsed] = useState(() => {
    try {
      return localStorage.getItem('codearena_roadmap_banner_collapsed') === 'true';
    } catch {
      return false;
    }
  });

  const toggleCollapse = () => {
    setIsCollapsed((prev) => {
      const next = !prev;
      try {
        localStorage.setItem('codearena_roadmap_banner_collapsed', String(next));
      } catch {
        // ignore storage errors
      }
      return next;
    });
  };

  return (
    <div className="relative mb-6 overflow-hidden rounded-2xl border border-slate-800/80 bg-gradient-to-b from-slate-900/90 via-[#10172a]/90 to-slate-950/90 shadow-2xl backdrop-blur-xl transition-all duration-300">
      {/* Ambient background glows */}
      <div className="absolute -top-16 -left-16 h-40 w-40 rounded-full bg-sky-500/10 blur-3xl pointer-events-none" />
      <div className="absolute -bottom-16 -right-16 h-40 w-40 rounded-full bg-indigo-500/10 blur-3xl pointer-events-none" />

      {/* Banner Header */}
      <div className="relative z-10 flex flex-wrap items-center justify-between gap-3 border-b border-slate-800/80 px-5 py-3.5 sm:px-6">
        <div className="flex items-center gap-3">
          <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl border border-sky-500/30 bg-sky-500/10 text-sky-400 shadow-md shadow-sky-500/10">
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
              <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
            </svg>
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-sm font-bold text-white tracking-tight">
                Structured Career Roadmaps
              </span>
              <span className="rounded-full border border-sky-500/30 bg-sky-500/15 px-2 py-0.5 text-[9px] font-bold uppercase tracking-wider text-sky-300">
                Job Readiness
              </span>
            </div>
            <p className="text-[11px] text-slate-400">
              Combine your DSA practice with production-grade engineering curricula
            </p>
          </div>
        </div>

        {/* Header Right Actions */}
        <div className="flex items-center gap-2">
          <Link
            to="/learning"
            className="inline-flex items-center gap-1.5 rounded-xl border border-sky-500/30 bg-sky-500/10 px-3 py-1.5 text-xs font-semibold text-sky-300 hover:border-sky-400 hover:bg-sky-500/20 hover:text-white transition-all shadow-sm"
          >
            <span>View All Tracks</span>
            <svg className="h-3.5 w-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <polyline points="9 18 15 12 9 6" />
            </svg>
          </Link>
          <button
            type="button"
            onClick={toggleCollapse}
            title={isCollapsed ? 'Expand Roadmaps' : 'Collapse Roadmaps'}
            className="flex h-8 w-8 items-center justify-center rounded-xl border border-slate-700/80 bg-slate-800/60 text-slate-400 hover:border-slate-600 hover:text-slate-200 transition-all"
          >
            <svg
              className={`h-4 w-4 transition-transform duration-200 ${isCollapsed ? '' : 'rotate-180'}`}
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              <polyline points="6 9 12 15 18 9" />
            </svg>
          </button>
        </div>
      </div>

      {/* Collapsible Content - Roadmaps Cards */}
      {!isCollapsed && (
        <div className="relative z-10 p-4 sm:p-5">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {TRACKS_PREVIEW.map((track) => (
              <div
                key={track.slug}
                className={`group relative flex flex-col justify-between rounded-xl border border-slate-800/90 bg-slate-900/60 p-4 sm:p-5 backdrop-blur-md transition-all duration-200 hover:-translate-y-0.5 ${track.borderGlow}`}
              >
                <div>
                  <div className="flex items-center justify-between gap-2 mb-2">
                    <span className={`rounded-md border px-2 py-0.5 text-[10px] font-bold uppercase tracking-wider ${track.badgeColor}`}>
                      {track.badge}
                    </span>
                    <div className="flex items-center gap-2 text-[11px] font-semibold text-slate-400">
                      <span><strong className="text-white">{track.phases}</strong> Phases</span>
                      <span>•</span>
                      <span><strong className="text-sky-300">{track.topics}</strong> Topics</span>
                      <span>•</span>
                      <span><strong className="text-emerald-300">{track.duration}</strong></span>
                    </div>
                  </div>

                  <h3 className="text-base font-bold text-white group-hover:text-sky-300 transition-colors">
                    {track.title}
                  </h3>
                  <p className="mt-1 text-xs text-slate-400 leading-relaxed line-clamp-2">
                    {track.summary}
                  </p>

                  {/* Skills tags */}
                  <div className="mt-3 flex flex-wrap gap-1.5">
                    {track.skills.map((skill) => (
                      <span
                        key={skill}
                        className="rounded-md border border-slate-800 bg-slate-950/70 px-2 py-0.5 text-[10px] font-medium text-slate-300"
                      >
                        {skill}
                      </span>
                    ))}
                  </div>
                </div>

                {/* Direct Action */}
                <div className="mt-4 pt-3 border-t border-slate-800/60 flex items-center justify-between">
                  <span className="text-[11px] font-medium text-slate-500">
                    Milestone-Driven Path
                  </span>
                  <Link
                    to={`/learning/${track.slug}`}
                    className="inline-flex items-center gap-1.5 rounded-lg border border-sky-500/40 bg-gradient-to-r from-sky-500/20 to-indigo-600/20 px-3 py-1.5 text-xs font-bold text-sky-200 hover:border-sky-400 hover:from-sky-500 hover:to-indigo-600 hover:text-white transition-all shadow-sm"
                  >
                    <span>Explore Track</span>
                    <svg className="h-3.5 w-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                      <polyline points="9 18 15 12 9 6" />
                    </svg>
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
});

export default CareerRoadmapBanner;
