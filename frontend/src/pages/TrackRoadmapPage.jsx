import React, { useEffect, useState, useRef, useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../services/api';
import Navbar from '../components/Navbar';
import RoadmapOverlay from '../components/RoadmapOverlay';

/* ── Gradient colour resolver ──────────────────────────────────── */
const GRADIENT_HEX = {
  'from-amber-500 to-orange-600':  { from: '#f59e0b', to: '#ea580c' },
  'from-blue-500 to-indigo-600':   { from: '#3b82f6', to: '#4f46e5' },
  'from-emerald-500 to-teal-600':  { from: '#10b981', to: '#0d9488' },
  'from-cyan-500 to-blue-600':     { from: '#06b6d4', to: '#2563eb' },
  'from-purple-500 to-indigo-600': { from: '#a855f7', to: '#4f46e5' },
  'from-red-500 to-rose-600':      { from: '#ef4444', to: '#e11d48' },
  'from-teal-500 to-green-600':    { from: '#14b8a6', to: '#16a34a' },
  'from-amber-500 to-yellow-600':  { from: '#f59e0b', to: '#ca8a04' },
  'from-green-500 to-emerald-600': { from: '#22c55e', to: '#059669' },
  'from-purple-500 to-pink-600':   { from: '#a855f7', to: '#db2777' },
};
const DEFAULT_GRAD = { from: '#38bdf8', to: '#6366f1' };
function getGrad(c) { return GRADIENT_HEX[c] || DEFAULT_GRAD; }

/* ── Floating ambient particles ────────────────────────────────── */
function AmbientParticles() {
  const particles = Array.from({ length: 18 }, (_, i) => ({
    id: i,
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    size: 2 + Math.random() * 3,
    delay: Math.random() * 6,
    duration: 6 + Math.random() * 8,
    opacity: 0.15 + Math.random() * 0.25,
  }));
  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none" aria-hidden>
      {particles.map((p) => (
        <div
          key={p.id}
          className="absolute rounded-full bg-sky-400/40"
          style={{
            left: p.left,
            top: p.top,
            width: p.size,
            height: p.size,
            opacity: p.opacity,
            animation: `float-particle ${p.duration}s ease-in-out ${p.delay}s infinite`,
          }}
        />
      ))}
    </div>
  );
}

/* ── Intersection Observer hook for scroll-reveal ──────────────── */
function useScrollReveal() {
  const [visibleIds, setVisibleIds] = useState(new Set());
  const observer = useRef(null);

  const observe = useCallback((node, id) => {
    if (!node) return;
    if (!observer.current) {
      observer.current = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (entry.isIntersecting) {
              setVisibleIds((prev) => new Set(prev).add(entry.target.dataset.revealId));
              observer.current?.unobserve(entry.target);
            }
          });
        },
        { threshold: 0.12, rootMargin: '0px 0px -40px 0px' }
      );
    }
    node.dataset.revealId = id;
    observer.current.observe(node);
  }, []);

  useEffect(() => {
    return () => observer.current?.disconnect();
  }, []);

  return { observe, isVisible: (id) => visibleIds.has(String(id)) };
}

/* ── Main component ────────────────────────────────────────────── */
export default function TrackRoadmapPage() {
  const { trackSlug } = useParams();
  const [track, setTrack] = useState(null);
  const [loading, setLoading] = useState(true);
  const [expandedPhases, setExpandedPhases] = useState({});
  const [showRoadmapOverlay, setShowRoadmapOverlay] = useState(false);
  const isAuth = api.isAuthenticated();
  const { observe, isVisible } = useScrollReveal();

  useEffect(() => {
    fetchRoadmap();
  }, [trackSlug]);

  const fetchRoadmap = async () => {
    try {
      setLoading(true);
      const data = await api.getTrackRoadmap(trackSlug || 'java-backend');
      setTrack(data);

      // Default expand all phases
      const expanded = {};
      (data.phases || []).forEach((p) => {
        expanded[p.id] = true;
      });
      setExpandedPhases(expanded);
    } catch (err) {
      console.error('Failed to load track roadmap:', err);
    } finally {
      setLoading(false);
    }
  };

  const togglePhase = (phaseId) => {
    setExpandedPhases((prev) => ({
      ...prev,
      [phaseId]: !prev[phaseId],
    }));
  };

  /* ── Loading skeleton with shimmer ─────────────────── */
  if (loading) {
    return (
      <div className="min-h-screen bg-[#0b1120] text-slate-100 flex flex-col">
        <Navbar />
        <div className="flex-1 max-w-5xl mx-auto w-full px-4 py-12 space-y-6">
          <div className="h-44 rounded-3xl bg-slate-900/40 border border-slate-800 animate-shimmer" />
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-24 rounded-3xl bg-slate-900/40 border border-slate-800 animate-shimmer" style={{ animationDelay: `${i * 0.2}s` }} />
          ))}
        </div>
      </div>
    );
  }

  if (!track) {
    return (
      <div className="min-h-screen bg-[#0b1120] text-slate-100 flex flex-col">
        <Navbar />
        <div className="flex-1 flex flex-col items-center justify-center text-center p-6">
          <h2 className="text-2xl font-bold text-white">Track Not Found</h2>
          <Link to="/learning" className="mt-4 text-sky-400 hover:underline">
            Return to Career Tracks
          </Link>
        </div>
      </div>
    );
  }

  const progress = track.userProgress;
  const completion = progress ? progress.completionPercentage : 0;
  const completedCount = progress ? progress.completedTopics : 0;
  const totalTopics = track.totalTopics || 40;

  // Find first uncompleted topic for "Resume Learning" button
  let nextTopic = null;
  if (track.phases) {
    for (const phase of track.phases) {
      for (const topic of phase.topics || []) {
        if (topic.status !== 'COMPLETED') {
          nextTopic = topic;
          break;
        }
      }
      if (nextTopic) break;
    }
  }

  return (
    <div className="min-h-screen bg-[#0b1120] text-slate-100 flex flex-col">
      {/* Background effects */}
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(56,189,248,0.1),transparent_35%),radial-gradient(circle_at_top_right,rgba(99,102,241,0.08),transparent_30%),linear-gradient(to_bottom,rgba(15,23,42,0.95),rgba(2,6,23,1))] pointer-events-none" />
      <AmbientParticles />

      <Navbar />

      <main className="relative z-10 flex-1 max-w-5xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-10">
        {/* Breadcrumb */}
        <div className="flex items-center gap-2 text-xs font-medium text-slate-400 mb-6 animate-fade-in-up">
          <Link to="/learning" className="hover:text-sky-400 transition-colors">
            Career Tracks
          </Link>
          <span>/</span>
          <span className="text-slate-200">{track.title}</span>
        </div>

        {/* ── Hero Card ─────────────────────────────────────── */}
        <div className="relative overflow-hidden rounded-3xl border border-slate-800 bg-gradient-to-b from-slate-900/90 to-slate-950/90 p-8 shadow-2xl backdrop-blur-xl mb-10 animate-fade-in-up card-glow" style={{ animationDelay: '0.05s' }}>
          {/* Animated accent glow */}
          <div className="absolute -top-24 -right-24 h-48 w-48 rounded-full bg-sky-500/10 blur-3xl animate-pulse-glow pointer-events-none" />
          <div className="absolute -bottom-16 -left-16 h-36 w-36 rounded-full bg-indigo-500/8 blur-3xl animate-pulse-glow pointer-events-none" style={{ animationDelay: '1.5s' }} />

          <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div className="max-w-2xl">
              <div className="inline-flex items-center gap-2 rounded-full border border-sky-500/30 bg-sky-500/10 px-3 py-1 text-xs font-semibold text-sky-300 mb-3">
                <span className="flex h-1.5 w-1.5 rounded-full bg-sky-400 animate-pulse" />
                Comprehensive Developer Track
              </div>
              <h1 className="text-2xl sm:text-4xl font-extrabold tracking-tight text-white">
                {track.title}
              </h1>
              <p className="mt-3 text-xs sm:text-sm text-slate-400 leading-relaxed">
                {track.description}
              </p>
              <div className="mt-4 flex flex-wrap items-center gap-4 text-xs font-semibold text-slate-400">
                <span className="flex items-center gap-1.5">
                  <span className="text-sky-400 font-bold">{track.totalPhases}</span> Milestones
                </span>
                <span>•</span>
                <span className="flex items-center gap-1.5">
                  <span className="text-sky-400 font-bold">{totalTopics}</span> Curated Lessons
                </span>
                <span>•</span>
                <span className="flex items-center gap-1.5">
                  <span className="text-emerald-400 font-bold">{track.estimatedWeeks}</span> Weeks Duration
                </span>
              </div>

              {/* Explore Roadmap Button */}
              <button
                onClick={() => setShowRoadmapOverlay(true)}
                className="mt-5 inline-flex items-center gap-2 rounded-xl border border-indigo-500/40 bg-indigo-500/10 px-5 py-2.5 text-xs font-bold uppercase tracking-wider text-indigo-200 shadow-sm transition-all hover:border-indigo-400 hover:bg-indigo-500/20 hover:text-white hover:shadow-indigo-500/10 hover:scale-[1.03] active:scale-[0.98] group"
              >
                <svg className="h-4 w-4 text-indigo-400 group-hover:text-indigo-300 transition-colors" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <circle cx="12" cy="12" r="10"/>
                  <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"/>
                </svg>
                <span>Explore Roadmap</span>
                <svg className="h-3.5 w-3.5 text-indigo-400 group-hover:translate-x-0.5 transition-transform" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <polyline points="9 18 15 12 9 6"/>
                </svg>
              </button>
            </div>

            {/* Quick Resume / Start Card */}
            <div className="flex flex-col gap-3 min-w-[220px]">
              {nextTopic && (
                <Link
                  to={`/learning/${track.slug}/${nextTopic.slug}`}
                  className="flex items-center justify-center gap-2 rounded-2xl border border-sky-500/40 bg-gradient-to-r from-sky-500 to-indigo-600 px-5 py-3 text-xs font-bold uppercase tracking-wider text-white shadow-lg shadow-sky-500/20 hover:from-sky-400 hover:to-indigo-500 hover:scale-[1.03] active:scale-[0.97] transition-all animate-hero-gradient"
                >
                  <span>{completedCount > 0 ? 'Resume Track' : 'Start Topic 1'}</span>
                  <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <polyline points="9 18 15 12 9 6" />
                  </svg>
                </Link>
              )}

              {isAuth && (
                <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-4">
                  <div className="flex items-center justify-between text-xs font-semibold mb-2">
                    <span className="text-slate-400">Overall Progress</span>
                    <span className="font-mono text-sky-400">{completion}%</span>
                  </div>
                  <div className="h-2 w-full overflow-hidden rounded-full bg-slate-800">
                    <div
                      className="h-full rounded-full bg-gradient-to-r from-sky-500 to-indigo-500 transition-all duration-700 ease-out"
                      style={{ width: `${completion}%` }}
                    />
                  </div>
                  <div className="mt-2 text-right text-[11px] text-slate-500">
                    {completedCount} / {totalTopics} completed
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* ── Phases & Topics List ──────────────────────────── */}
        <div className="space-y-6">
          {(track.phases || []).map((phase, phaseIndex) => {
            const isExpanded = !!expandedPhases[phase.id];
            const phaseTopics = phase.topics || [];
            const phaseCompleted = phaseTopics.filter((t) => t.status === 'COMPLETED').length;
            const phaseProgress = phaseTopics.length > 0 ? Math.round((phaseCompleted / phaseTopics.length) * 100) : 0;
            const grad = getGrad(phase.color);
            const revealed = isVisible(phase.id);

            return (
              <div
                key={phase.id}
                ref={(node) => observe(node, phase.id)}
                className={`overflow-hidden rounded-3xl border border-slate-800 bg-slate-950/60 shadow-xl backdrop-blur-md transition-all duration-500 ${
                  revealed ? 'animate-fade-in-up opacity-100' : 'opacity-0'
                }`}
                style={{
                  animationDelay: revealed ? `${phaseIndex * 0.06}s` : undefined,
                  '--phase-accent': grad.from,
                }}
              >
                {/* Phase Header Accordion */}
                <button
                  onClick={() => togglePhase(phase.id)}
                  className="w-full flex items-center justify-between p-6 text-left border-b border-slate-800/80 hover:bg-slate-900/40 transition-all duration-200 group/phase"
                >
                  <div className="flex items-start sm:items-center gap-4">
                    {/* Phase number badge — now uses phase colour */}
                    <div
                      className="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl border font-mono text-sm font-black shadow-md transition-transform duration-200 group-hover/phase:scale-110"
                      style={{
                        borderColor: `${grad.from}55`,
                        background: `linear-gradient(135deg, ${grad.from}18, ${grad.to}18)`,
                        color: grad.from,
                        boxShadow: `0 0 16px ${grad.from}15`,
                      }}
                    >
                      {phase.phaseNumber < 10 ? `0${phase.phaseNumber}` : phase.phaseNumber}
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <span
                          className="text-[10px] uppercase font-bold tracking-wider"
                          style={{ color: grad.from }}
                        >
                          Phase {phase.phaseNumber}
                        </span>
                        <span className="text-slate-600">•</span>
                        <span className="text-[11px] text-slate-400 font-medium">
                          ~{phase.estimatedHours}h total effort
                        </span>
                      </div>
                      <h2 className="text-base sm:text-lg font-bold text-white mt-0.5 group-hover/phase:text-slate-100 transition-colors">
                        {phase.title}
                      </h2>
                    </div>
                  </div>

                  <div className="flex items-center gap-3">
                    {/* Mini progress bar for the phase */}
                    <div className="hidden sm:flex items-center gap-2">
                      <div className="w-16 h-1.5 rounded-full bg-slate-800 overflow-hidden">
                        <div
                          className="h-full rounded-full transition-all duration-700"
                          style={{
                            width: `${phaseProgress}%`,
                            background: `linear-gradient(90deg, ${grad.from}, ${grad.to})`,
                          }}
                        />
                      </div>
                      <span className="rounded-full border border-slate-800 bg-slate-900 px-2.5 py-0.5 text-[11px] font-semibold text-slate-300 tabular-nums">
                        {phaseCompleted}/{phaseTopics.length}
                      </span>
                    </div>
                    <svg
                      className={`h-5 w-5 text-slate-400 transition-transform duration-300 ${
                        isExpanded ? 'rotate-180' : ''
                      }`}
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <polyline points="6 9 12 15 18 9" />
                    </svg>
                  </div>
                </button>

                {/* Topics Container — animated accordion */}
                {isExpanded && (
                  <div className="divide-y divide-slate-800/60 p-2 sm:p-4 bg-slate-900/20 animate-accordion-slide">
                    {phaseTopics.map((topic, topicIndex) => {
                      const isCompleted = topic.status === 'COMPLETED';
                      const isInProgress = topic.status === 'IN_PROGRESS';

                      return (
                        <Link
                          key={topic.slug}
                          to={`/learning/${track.slug}/${topic.slug}`}
                          className="group flex flex-col sm:flex-row sm:items-center justify-between gap-3 p-4 rounded-2xl hover:bg-slate-900/70 transition-all duration-200 border border-transparent hover:border-slate-800 hover:shadow-lg hover:shadow-slate-900/50"
                          style={{
                            animationDelay: `${topicIndex * 0.03}s`,
                          }}
                        >
                          <div className="flex items-start gap-3.5">
                            {/* Status Icon */}
                            <div className="mt-1 shrink-0">
                              {isCompleted ? (
                                <div className="flex h-6 w-6 items-center justify-center rounded-full bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 shadow-sm shadow-emerald-500/20">
                                  <svg className="h-3.5 w-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                                    <polyline points="20 6 9 17 4 12" />
                                  </svg>
                                </div>
                              ) : isInProgress ? (
                                <div className="flex h-6 w-6 items-center justify-center rounded-full bg-sky-500/20 text-sky-400 border border-sky-500/40">
                                  <div className="h-2 w-2 rounded-full bg-sky-400 animate-ping" />
                                </div>
                              ) : (
                                <div
                                  className="flex h-6 w-6 items-center justify-center rounded-full border bg-slate-800/60 text-slate-500 group-hover:border-slate-600 transition-colors"
                                  style={{ borderColor: `${grad.from}30` }}
                                >
                                  <span className="text-[10px] font-mono font-semibold">{topic.sequenceOrder}</span>
                                </div>
                              )}
                            </div>

                            {/* Topic Info */}
                            <div>
                              <div className="flex flex-wrap items-center gap-2">
                                <h3 className="text-sm font-semibold text-slate-100 group-hover:text-sky-300 transition-colors">
                                  {topic.title}
                                </h3>
                                {topic.tier === 'ADVANCED' && (
                                  <span className="rounded-md border border-purple-500/30 bg-purple-500/10 px-2 py-0.5 text-[9px] font-bold text-purple-300">
                                    ADVANCED
                                  </span>
                                )}
                                {topic.hasProject && (
                                  <span className="rounded-md border border-amber-500/30 bg-amber-500/10 px-2 py-0.5 text-[9px] font-bold text-amber-300">
                                    PROJECT
                                  </span>
                                )}
                              </div>
                              <p className="mt-1 text-xs text-slate-400 line-clamp-1 group-hover:text-slate-300 transition-colors">
                                {topic.summary}
                              </p>
                            </div>
                          </div>

                          {/* Metadata Badges */}
                          <div className="flex items-center gap-3 shrink-0 self-end sm:self-center">
                            <span className="text-[11px] font-mono text-slate-400" title="Estimated Reading Time">
                              {topic.estimatedMinutes}m read
                            </span>
                            {topic.totalLearningHours && (
                              <span className="hidden sm:inline-block text-[11px] font-mono text-slate-500" title="Total Hands-on Effort">
                                • ~{topic.totalLearningHours}h practice
                              </span>
                            )}
                            <span
                              className="flex items-center gap-1 rounded-xl border border-slate-700/80 bg-slate-800/60 px-3 py-1 text-xs font-semibold text-slate-300 group-hover:text-white transition-all"
                              style={{
                                borderColor: isCompleted ? '#10b98140' : undefined,
                              }}
                            >
                              <span>{isCompleted ? 'Review' : 'Start'}</span>
                              <svg className="h-3.5 w-3.5 text-slate-400 group-hover:text-sky-400 group-hover:translate-x-0.5 transition-all" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                                <polyline points="9 18 15 12 9 6" />
                              </svg>
                            </span>
                          </div>
                        </Link>
                      );
                    })}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </main>

      {/* Animated Roadmap Overlay */}
      {showRoadmapOverlay && track.phases && (
        <RoadmapOverlay
          phases={track.phases}
          trackTitle={track.title}
          onClose={() => setShowRoadmapOverlay(false)}
        />
      )}
    </div>
  );
}
