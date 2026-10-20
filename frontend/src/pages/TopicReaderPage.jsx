import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { api } from '../services/api';
import Navbar from '../components/Navbar';
import MarkdownViewer from '../components/MarkdownViewer';
import { getTopicMarkdown } from '../utils/contentLoader';

export default function TopicReaderPage() {
  const { trackSlug, topicSlug } = useParams();
  const navigate = useNavigate();

  const [topic, setTopic] = useState(null);
  const [markdownContent, setMarkdownContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);

  // Sibling topics for Prev/Next navigation
  const [prevTopic, setPrevTopic] = useState(null);
  const [nextTopic, setNextTopic] = useState(null);

  useEffect(() => {
    fetchTopicAndContent();
  }, [trackSlug, topicSlug]);

  const fetchTopicAndContent = async () => {
    try {
      setLoading(true);
      // 1. Fetch topic metadata
      const topicData = await api.getTopicDetails(topicSlug);
      setTopic(topicData);
      setIsCompleted(topicData.status === 'COMPLETED');

      // 2. Fetch markdown file content using our contentLoader
      const rawMarkdown = getTopicMarkdown(topicData.contentPath);
      if (rawMarkdown) {
        setMarkdownContent(rawMarkdown);
      } else {
        setMarkdownContent(`# ${topicData.title}\n\n> Lesson notes for this topic are currently being finalized according to the 9-step pedagogical standard.\n\n### Overview\n${topicData.summary}\n\n**Estimated Reading:** ${topicData.estimatedMinutes} minutes  \n**Hands-on Practice Effort:** ~${topicData.totalLearningHours || 2.5} hours`);
      }

      // 3. Mark as IN_PROGRESS if not already completed and user is logged in
      if (api.isAuthenticated() && topicData.status === 'NOT_STARTED') {
        try {
          await api.updateTopicProgress(topicSlug, 'IN_PROGRESS');
        } catch (e) {
          // Silent non-critical update
        }
      }

      // 4. Determine Prev / Next topics from roadmap
      try {
        const roadmap = await api.getTrackRoadmap(trackSlug || 'java-backend');
        const allTopics = [];
        (roadmap.phases || []).forEach((p) => {
          (p.topics || []).forEach((t) => allTopics.push(t));
        });

        const currentIndex = allTopics.findIndex((t) => t.slug === topicSlug);
        if (currentIndex > 0) {
          setPrevTopic(allTopics[currentIndex - 1]);
        } else {
          setPrevTopic(null);
        }
        if (currentIndex >= 0 && currentIndex < allTopics.length - 1) {
          setNextTopic(allTopics[currentIndex + 1]);
        } else {
          setNextTopic(null);
        }
      } catch (err) {
        console.warn('Could not determine sibling topics:', err);
      }
    } catch (err) {
      console.error('Failed to load topic:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleComplete = async () => {
    if (!api.isAuthenticated()) {
      navigate('/login');
      return;
    }

    try {
      setUpdating(true);
      const newStatus = isCompleted ? 'IN_PROGRESS' : 'COMPLETED';
      await api.updateTopicProgress(topicSlug, newStatus);
      setIsCompleted(newStatus === 'COMPLETED');
    } catch (err) {
      console.error('Failed to update topic status:', err);
    } finally {
      setUpdating(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0b1120] text-slate-100 flex flex-col">
        <Navbar />
        <div className="flex-1 max-w-4xl mx-auto w-full px-4 py-12 space-y-6">
          <div className="h-10 w-1/3 rounded-xl bg-slate-900/60 animate-pulse border border-slate-800" />
          <div className="h-64 rounded-3xl bg-slate-900/40 animate-pulse border border-slate-800" />
          <div className="h-96 rounded-3xl bg-slate-900/40 animate-pulse border border-slate-800" />
        </div>
      </div>
    );
  }

  if (!topic) {
    return (
      <div className="min-h-screen bg-[#0b1120] text-slate-100 flex flex-col">
        <Navbar />
        <div className="flex-1 flex flex-col items-center justify-center text-center p-6">
          <h2 className="text-2xl font-bold text-white">Topic Not Found</h2>
          <Link to={`/learning/${trackSlug}`} className="mt-4 text-sky-400 hover:underline">
            Back to Roadmap
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0b1120] text-slate-100 flex flex-col">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(56,189,248,0.08),transparent_35%),radial-gradient(circle_at_top_right,rgba(99,102,241,0.06),transparent_30%),linear-gradient(to_bottom,rgba(15,23,42,0.95),rgba(2,6,23,1))] pointer-events-none" />

      <Navbar />

      <main className="relative z-10 flex-1 max-w-4xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-8">
        {/* Navigation Bar / Breadcrumb */}
        <div className="flex flex-wrap items-center justify-between gap-3 pb-6 border-b border-slate-800/80 mb-8">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-400">
            <Link to="/learning" className="hover:text-sky-400 transition-colors">
              Tracks
            </Link>
            <span>/</span>
            <Link to={`/learning/${trackSlug}`} className="hover:text-sky-400 transition-colors capitalize">
              {trackSlug?.replace('-', ' ')}
            </Link>
            <span>/</span>
            <span className="text-slate-200 font-semibold truncate max-w-[200px] sm:max-w-none">
              {topic.title}
            </span>
          </div>

          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2 text-xs font-mono text-slate-400">
              <span className="flex items-center gap-1">
                <svg className="h-4 w-4 text-sky-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10" />
                  <polyline points="12 6 12 12 16 14" />
                </svg>
                {topic.estimatedMinutes}m read
              </span>
              {topic.totalLearningHours && (
                <span className="hidden sm:inline-block text-slate-500">
                  • ~{topic.totalLearningHours}h practice
                </span>
              )}
            </div>

            {topic.tier === 'ADVANCED' && (
              <span className="rounded-md border border-purple-500/40 bg-purple-500/15 px-2 py-0.5 text-[10px] font-bold text-purple-300">
                ADVANCED
              </span>
            )}

            {/* Quick Completion Button */}
            <button
              onClick={handleToggleComplete}
              disabled={updating}
              className={`flex items-center gap-1.5 rounded-xl border px-3 py-1.5 text-xs font-bold transition-all ${
                isCompleted
                  ? 'border-emerald-500/40 bg-emerald-500/15 text-emerald-300 shadow-sm'
                  : 'border-slate-700 bg-slate-900 text-slate-300 hover:border-sky-500/40 hover:text-white'
              }`}
            >
              {isCompleted ? (
                <>
                  <svg className="h-4 w-4 text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                  <span>Completed</span>
                </>
              ) : (
                <>
                  <div className="h-3.5 w-3.5 rounded-full border border-slate-500" />
                  <span>Mark as Done</span>
                </>
              )}
            </button>
          </div>
        </div>

        {/* Markdown Reader Body */}
        <article className="rounded-3xl border border-slate-800 bg-slate-950/60 p-6 sm:p-10 shadow-2xl backdrop-blur-xl">
          <MarkdownViewer content={markdownContent} />
        </article>

        {/* Bottom Pagination & Mastery Bar */}
        <div className="mt-10 flex flex-col sm:flex-row items-center justify-between gap-4 pt-6 border-t border-slate-800/80">
          {prevTopic ? (
            <Link
              to={`/learning/${trackSlug}/${prevTopic.slug}`}
              className="flex items-center gap-2 text-xs font-semibold text-slate-400 hover:text-white transition-colors"
            >
              <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <polyline points="15 18 9 12 15 6" />
              </svg>
              <div className="text-left">
                <div className="text-[10px] text-slate-500 uppercase">Previous</div>
                <div className="truncate max-w-[150px] sm:max-w-[200px]">{prevTopic.title}</div>
              </div>
            </Link>
          ) : (
            <div />
          )}

          <button
            onClick={handleToggleComplete}
            disabled={updating}
            className={`w-full sm:w-auto flex items-center justify-center gap-2 rounded-2xl px-6 py-3 text-xs font-bold uppercase tracking-wider transition-all shadow-lg ${
              isCompleted
                ? 'border border-emerald-500/40 bg-emerald-500/20 text-emerald-300'
                : 'border border-sky-500/40 bg-gradient-to-r from-sky-500 to-indigo-600 text-white shadow-sky-500/20 hover:from-sky-400 hover:to-indigo-500'
            }`}
          >
            {isCompleted ? (
              <>
                <svg className="h-4 w-4 text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <polyline points="20 6 9 17 4 12" />
                </svg>
                <span>Topic Mastered! (Click to Undo)</span>
              </>
            ) : (
              <>
                <span>Mark Topic as Complete</span>
                <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <polyline points="20 6 9 17 4 12" />
                </svg>
              </>
            )}
          </button>

          {nextTopic ? (
            <Link
              to={`/learning/${trackSlug}/${nextTopic.slug}`}
              className="flex items-center gap-2 text-xs font-semibold text-slate-400 hover:text-white transition-colors"
            >
              <div className="text-right">
                <div className="text-[10px] text-slate-500 uppercase">Next</div>
                <div className="truncate max-w-[150px] sm:max-w-[200px]">{nextTopic.title}</div>
              </div>
              <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <polyline points="9 18 15 12 9 6" />
              </svg>
            </Link>
          ) : (
            <div />
          )}
        </div>
      </main>
    </div>
  );
}
