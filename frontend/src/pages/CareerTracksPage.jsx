import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../services/api';
import Navbar from '../components/Navbar';

export default function CareerTracksPage() {
  const [tracks, setTracks] = useState([]);
  const [loading, setLoading] = useState(true);
  const isAuth = api.isAuthenticated();

  useEffect(() => {
    fetchTracks();
  }, []);

  const fetchTracks = async () => {
    try {
      setLoading(true);
      const data = await api.getLearningTracks();
      setTracks(data);
    } catch (err) {
      console.error('Failed to load career tracks:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0b1120] text-slate-100 flex flex-col">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(56,189,248,0.12),transparent_35%),radial-gradient(circle_at_top_right,rgba(99,102,241,0.1),transparent_30%),linear-gradient(to_bottom,rgba(15,23,42,0.95),rgba(2,6,23,1))] pointer-events-none" />

      {/* Ambient floating dots */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none" aria-hidden>
        {Array.from({ length: 12 }, (_, i) => (
          <div
            key={i}
            className="absolute rounded-full bg-sky-400/30"
            style={{
              left: `${10 + Math.random() * 80}%`,
              top: `${10 + Math.random() * 80}%`,
              width: 2 + (i % 3),
              height: 2 + (i % 3),
              opacity: 0.15 + (i % 4) * 0.08,
              animation: `float-particle ${7 + i * 0.6}s ease-in-out ${i * 0.5}s infinite`,
            }}
          />
        ))}
      </div>

      <Navbar />

      <main className="relative z-10 flex-1 max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-10">
        {/* Hero Section */}
        <div className="mb-12 text-center max-w-3xl mx-auto animate-fade-in-up">
          <div className="inline-flex items-center gap-2 rounded-full border border-sky-500/30 bg-sky-500/10 px-3.5 py-1 text-xs font-semibold text-sky-300 shadow-sm mb-4">
            <span className="flex h-2 w-2 rounded-full bg-sky-400 animate-pulse" />
            Developing Developers Standard
          </div>
          <h1 className="text-3xl sm:text-5xl font-black tracking-tight text-white leading-tight">
            Curated Career Paths to{' '}
            <span className="bg-gradient-to-r from-sky-400 via-indigo-300 to-cyan-400 bg-clip-text text-transparent animate-hero-gradient">
              Production Mastery
            </span>
          </h1>
          <p className="mt-4 text-sm sm:text-base text-slate-400 leading-relaxed">
            Stop wandering across disconnected tutorials. Follow milestone-driven roadmaps that teach
            architecture, under-the-hood mechanics, interview defense, and direct hands-on DSA practice.
          </p>
        </div>

        {/* Tracks Grid */}
        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {[1, 2].map((i) => (
              <div key={i} className="h-72 rounded-2xl border border-slate-800 bg-slate-900/40 animate-shimmer" style={{ animationDelay: `${i * 0.3}s` }} />
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {tracks.map((track, trackIdx) => {
              const progress = track.userProgress;
              const completion = progress ? progress.completionPercentage : 0;
              const completedCount = progress ? progress.completedTopics : 0;

              return (
                <div
                  key={track.slug}
                  className="group relative flex flex-col justify-between overflow-hidden rounded-3xl border border-slate-800 bg-gradient-to-b from-slate-900/90 to-slate-950/90 p-7 shadow-2xl backdrop-blur-xl transition-all duration-300 hover:border-sky-500/40 hover:shadow-sky-500/10 hover:-translate-y-1 card-glow animate-fade-in-up"
                  style={{ animationDelay: `${0.15 + trackIdx * 0.1}s` }}
                >
                  {/* Hover glow blob */}
                  <div className="absolute top-0 right-0 -mr-16 -mt-16 h-36 w-36 rounded-full bg-sky-500/10 blur-2xl group-hover:bg-sky-500/20 group-hover:scale-125 transition-all duration-500" />

                  <div className="relative z-10">
                    {/* Header: Icon + Badge */}
                    <div className="flex items-center justify-between mb-5">
                      <div className="flex h-12 w-12 items-center justify-center rounded-2xl border border-sky-500/30 bg-sky-500/10 text-sky-400 shadow-lg shadow-sky-500/10 group-hover:scale-110 group-hover:shadow-sky-500/25 transition-all duration-300">
                        <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                          <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
                        </svg>
                      </div>
                      <span className="rounded-full border border-emerald-500/30 bg-emerald-500/15 px-3 py-1 text-[11px] font-bold uppercase tracking-wider text-emerald-300">
                        Active Track
                      </span>
                    </div>

                    {/* Title & Description */}
                    <h2 className="text-xl font-bold tracking-tight text-white group-hover:text-sky-300 transition-colors">
                      {track.title}
                    </h2>
                    <p className="mt-2.5 text-xs sm:text-sm text-slate-400 leading-relaxed line-clamp-3">
                      {track.description}
                    </p>

                    {/* Stats Pill Row */}
                    <div className="mt-5 grid grid-cols-3 gap-2 border-t border-slate-800/80 pt-4 text-center">
                      <div className="rounded-xl bg-slate-900/60 p-2 group-hover:bg-slate-900/80 transition-colors">
                        <div className="text-base font-bold text-white">{track.totalPhases}</div>
                        <div className="text-[10px] uppercase font-semibold text-slate-500">Phases</div>
                      </div>
                      <div className="rounded-xl bg-slate-900/60 p-2 group-hover:bg-slate-900/80 transition-colors">
                        <div className="text-base font-bold text-sky-300">{track.totalTopics}</div>
                        <div className="text-[10px] uppercase font-semibold text-slate-500">Topics</div>
                      </div>
                      <div className="rounded-xl bg-slate-900/60 p-2 group-hover:bg-slate-900/80 transition-colors">
                        <div className="text-base font-bold text-emerald-300">{track.estimatedWeeks} wks</div>
                        <div className="text-[10px] uppercase font-semibold text-slate-500">Duration</div>
                      </div>
                    </div>

                    {/* Progress Bar (if authenticated) */}
                    {isAuth && (
                      <div className="mt-5 rounded-xl border border-slate-800/80 bg-slate-900/50 p-3">
                        <div className="flex items-center justify-between text-xs font-semibold mb-1.5">
                          <span className="text-slate-400">Your Progress</span>
                          <span className="text-sky-400 font-mono">{completion}%</span>
                        </div>
                        <div className="h-2 w-full overflow-hidden rounded-full bg-slate-800">
                          <div
                            className="h-full rounded-full bg-gradient-to-r from-sky-500 to-indigo-500 transition-all duration-700 ease-out"
                            style={{ width: `${completion}%` }}
                          />
                        </div>
                        <div className="mt-1.5 text-[11px] text-slate-500">
                          {completedCount} of {track.totalTopics} topics mastered
                        </div>
                      </div>
                    )}
                  </div>

                  {/* Action Link */}
                  <div className="relative z-10 mt-6 pt-4 border-t border-slate-800/80">
                    <Link
                      to={`/learning/${track.slug}`}
                      className="flex w-full items-center justify-center gap-2 rounded-xl border border-sky-500/40 bg-sky-500/10 px-4 py-2.5 text-xs font-bold uppercase tracking-wider text-sky-200 shadow-sm transition-all hover:border-sky-400 hover:bg-sky-500/20 hover:text-white hover:scale-[1.03] active:scale-[0.97]"
                    >
                      <span>Explore Roadmap</span>
                      <svg className="h-4 w-4 group-hover:translate-x-0.5 transition-transform" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                        <polyline points="9 18 15 12 9 6" />
                      </svg>
                    </Link>
                  </div>
                </div>
              );
            })}

            {/* Future Tracks Coming Soon Cards */}
            <div className="relative flex flex-col justify-between overflow-hidden rounded-3xl border border-dashed border-slate-800 bg-slate-950/40 p-7 backdrop-blur-sm opacity-70 animate-fade-in-up" style={{ animationDelay: '0.35s' }}>
              <div>
                <div className="flex items-center justify-between mb-5">
                  <div className="flex h-12 w-12 items-center justify-center rounded-2xl border border-slate-800 bg-slate-900 text-slate-500">
                    <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <circle cx="12" cy="12" r="10" />
                      <line x1="12" y1="8" x2="12" y2="12" />
                      <line x1="12" y1="16" x2="12.01" y2="16" />
                    </svg>
                  </div>
                  <span className="rounded-full border border-slate-700 bg-slate-900 px-3 py-1 text-[11px] font-bold uppercase tracking-wider text-slate-400">
                    Coming Soon
                  </span>
                </div>
                <h2 className="text-xl font-bold tracking-tight text-slate-300">
                  Python Backend & Distributed Systems
                </h2>
                <p className="mt-2.5 text-xs sm:text-sm text-slate-500 leading-relaxed">
                  FastAPI, AsyncIO event loops, Celery task queues, PostgreSQL, Docker sandboxing, and microservice architectures.
                </p>
              </div>
              <div className="mt-6 pt-4 border-t border-slate-800/80">
                <button
                  disabled
                  className="w-full rounded-xl border border-slate-800 bg-slate-900/50 py-2.5 text-xs font-bold uppercase tracking-wider text-slate-500 cursor-not-allowed"
                >
                  In Development
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
