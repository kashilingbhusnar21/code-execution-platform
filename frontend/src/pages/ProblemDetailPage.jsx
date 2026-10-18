import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';

const ProblemDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [problem, setProblem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSolved, setIsSolved] = useState(false);

  useEffect(() => {
    fetchProblem();
    if (api.isAuthenticated()) {
      fetchProgress();
    }
  }, [id]);

  const fetchProblem = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.getProblemById(id);
      setProblem(data);
    } catch (err) {
      setError('Failed to load problem');
      console.error('Error fetching problem:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchProgress = async () => {
    try {
      const progress = await api.getUserProgress();
      if (progress && progress.solvedProblemIds) {
        const solved = progress.solvedProblemIds.some((pId) => String(pId) === String(id));
        setIsSolved(solved);
      }
    } catch (err) {
      console.error('Error checking solved status:', err);
    }
  };

  const getDifficultyColor = (difficulty) => {
    switch (difficulty) {
      case 'EASY':
        return 'bg-emerald-500/15 text-emerald-300 border-emerald-500/30';
      case 'MEDIUM':
        return 'bg-amber-500/15 text-amber-300 border-amber-500/30';
      case 'HARD':
        return 'bg-rose-500/15 text-rose-300 border-rose-500/30';
      default:
        return 'bg-slate-800 text-slate-300 border-slate-700';
    }
  };

  const handleSolve = () => {
    navigate(`/editor?problemId=${id}`);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0b1120] text-slate-100 flex items-center justify-center">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-cyan-400"></div>
          <p className="mt-4 text-sm text-slate-400">Loading problem details...</p>
        </div>
      </div>
    );
  }

  if (error || !problem) {
    return (
      <div className="min-h-screen bg-[#0b1120] text-slate-100 flex items-center justify-center">
        <div className="text-center max-w-md p-8 rounded-2xl border border-slate-800/80 bg-slate-900/60 shadow-xl">
          <p className="text-rose-400 text-lg font-medium">{error || 'Problem not found'}</p>
          <Link
            to="/problems"
            className="mt-4 inline-flex items-center gap-1.5 rounded-xl border border-cyan-500/30 bg-cyan-500/10 px-4 py-2 text-sm font-medium text-cyan-300 hover:bg-cyan-500/20 transition-all"
          >
            ← Back to Problem List
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0b1120] text-slate-100">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(59,130,246,0.12),transparent_30%),radial-gradient(circle_at_top_right,rgba(16,185,129,0.08),transparent_25%),linear-gradient(to_bottom,rgba(15,23,42,0.95),rgba(2,6,23,1))] pointer-events-none" />

      {/* Top Header */}
      <header className="relative z-30 border-b border-slate-800/80 bg-slate-950/80 backdrop-blur-xl">
        <div className="mx-auto flex h-16 w-full max-w-7xl items-center justify-between gap-3 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center gap-3">
            <Link to="/problems" className="flex items-center gap-3 group">
              <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-sky-500/20 bg-sky-500/10 text-sky-300 shadow-lg shadow-sky-500/10">
                <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
                  <path d="M16 18l6-6-6-6" strokeLinecap="round" strokeLinejoin="round" />
                  <path d="M8 6l-6 6 6 6" strokeLinecap="round" strokeLinejoin="round" />
                  <path d="M14 4L10 20" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              </div>
              <div>
                <h1 className="text-lg font-semibold tracking-tight text-slate-50">CodeArena</h1>
                <p className="truncate text-xs font-medium text-cyan-400/90 tracking-wide">
                  Built by Kashiling Bhusnar
                </p>
              </div>
            </Link>

            <nav className="flex items-center gap-1.5 border-l border-slate-800 pl-3 ml-2">
              <Link
                to="/problems"
                className="flex items-center gap-1.5 rounded-xl border border-cyan-400/70 bg-cyan-500/25 px-3 py-1.5 text-xs font-semibold text-white shadow-md shadow-cyan-500/20 ring-1 ring-cyan-400/30"
              >
                <svg
                  className="h-4 w-4 text-cyan-300"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <polyline points="16 18 22 12 16 6" />
                  <polyline points="8 6 2 12 8 18" />
                </svg>
                <span>DSA Problems</span>
              </Link>
            </nav>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={handleSolve}
              className="inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-sky-500 to-cyan-400 px-4 py-2 text-xs sm:text-sm font-semibold text-slate-950 shadow-md shadow-cyan-500/20 hover:brightness-110 transition-all"
            >
              <span>Solve Problem</span>
              <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
              </svg>
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Navigation Breadcrumb & Title */}
        <div className="mb-6">
          <Link
            to="/problems"
            className="inline-flex items-center gap-1.5 text-xs font-medium text-cyan-400 hover:text-cyan-300 mb-3"
          >
            ← Back to DSA Problems
          </Link>
          <div className="flex flex-wrap items-center gap-3 mb-3">
            <h1 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">
              {problem.id}. {problem.title}
            </h1>
            <span
              className={`inline-block px-3 py-1 rounded-full text-xs font-semibold border ${getDifficultyColor(
                problem.difficulty
              )}`}
            >
              {problem.difficulty}
            </span>
            {isSolved && (
              <span className="inline-flex items-center gap-1.5 rounded-full border border-emerald-500/30 bg-emerald-500/15 px-3 py-1 text-xs font-semibold text-emerald-300">
                <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
                <span>Solved</span>
              </span>
            )}
          </div>

          <div className="flex gap-2 flex-wrap">
            {problem.topics && problem.topics.map((topic) => (
              <span
                key={topic.id}
                className="rounded-md border border-slate-700 bg-slate-800/80 px-2.5 py-1 text-xs font-medium text-sky-300"
              >
                {topic.name}
              </span>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Problem Description & Details */}
          <div className="lg:col-span-2 space-y-6">
            <div className="rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl shadow-xl shadow-black/20">
              <h2 className="text-base font-semibold text-slate-200 uppercase tracking-wider mb-3">
                Description
              </h2>
              <div className="prose prose-invert max-w-none text-slate-300 whitespace-pre-line text-sm leading-relaxed">
                {problem.description}
              </div>
            </div>

            {/* Constraints */}
            {problem.constraints && (
              <div className="rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl shadow-xl shadow-black/20">
                <h2 className="text-base font-semibold text-slate-200 uppercase tracking-wider mb-3">
                  Constraints
                </h2>
                <div className="text-slate-300 whitespace-pre-line text-sm leading-relaxed font-mono bg-slate-950/60 p-4 rounded-xl border border-slate-800">
                  {problem.constraints}
                </div>
              </div>
            )}

            {/* Sample Test Cases */}
            {problem.sampleTestCases && problem.sampleTestCases.length > 0 && (
              <div className="rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl shadow-xl shadow-black/20">
                <h2 className="text-base font-semibold text-slate-200 uppercase tracking-wider mb-4">
                  Sample Test Cases
                </h2>
                <div className="space-y-4">
                  {problem.sampleTestCases.map((testCase, index) => (
                    <div
                      key={testCase.id || index}
                      className="border border-slate-800 rounded-xl p-4 bg-slate-950/60"
                    >
                      <div className="mb-2">
                        <h3 className="font-semibold text-sm text-cyan-300">
                          Example {index + 1}
                        </h3>
                        {testCase.explanation && (
                          <p className="text-xs text-slate-400 mt-1">
                            {testCase.explanation}
                          </p>
                        )}
                      </div>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mt-3">
                        <div>
                          <label className="block text-xs font-medium text-slate-400 mb-1">
                            Input:
                          </label>
                          <pre className="bg-slate-900/90 text-slate-200 p-2.5 rounded-lg text-xs font-mono overflow-x-auto border border-slate-800">
                            {testCase.input}
                          </pre>
                        </div>
                        <div>
                          <label className="block text-xs font-medium text-slate-400 mb-1">
                            Expected Output:
                          </label>
                          <pre className="bg-slate-900/90 text-emerald-300 p-2.5 rounded-lg text-xs font-mono overflow-x-auto border border-slate-800">
                            {testCase.expectedOutput}
                          </pre>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Quick Solve Action */}
            <div className="rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl shadow-xl shadow-black/20">
              <h2 className="text-base font-semibold text-slate-200 mb-3">Ready to Code?</h2>
              <p className="text-xs text-slate-400 mb-4">
                Open this problem in CodeArena's IDE with pre-configured Java, Python, and C++ starter templates and real-time grading.
              </p>
              <button
                onClick={handleSolve}
                className="w-full flex items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-sky-500 to-cyan-400 py-3 px-4 text-sm font-semibold text-slate-950 shadow-lg shadow-cyan-500/20 hover:brightness-110 transition-all"
              >
                <span>Open in Code Editor</span>
                <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
                </svg>
              </button>
            </div>

            {/* Problem Limits Info */}
            <div className="rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl shadow-xl shadow-black/20">
              <h2 className="text-base font-semibold text-slate-200 mb-4">Execution Limits</h2>
              <div className="space-y-3 text-sm">
                <div className="flex justify-between items-center py-1 border-b border-slate-800/60">
                  <span className="text-slate-400 text-xs">Time Limit:</span>
                  <span className="font-mono font-medium text-slate-200">{problem.timeLimit || 2000} ms</span>
                </div>
                <div className="flex justify-between items-center py-1">
                  <span className="text-slate-400 text-xs">Memory Limit:</span>
                  <span className="font-mono font-medium text-slate-200">{problem.memoryLimit || 256} MB</span>
                </div>
              </div>
            </div>

            {/* Topics */}
            {problem.topics && problem.topics.length > 0 && (
              <div className="rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl shadow-xl shadow-black/20">
                <h2 className="text-base font-semibold text-slate-200 mb-3">Topics</h2>
                <div className="flex flex-wrap gap-2">
                  {problem.topics.map((topic) => (
                    <span
                      key={topic.id}
                      className="rounded-md border border-slate-700 bg-slate-800/80 px-2.5 py-1 text-xs font-medium text-sky-300"
                    >
                      {topic.name}
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProblemDetailPage;
