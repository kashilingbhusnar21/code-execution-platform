import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../services/api';
import DsaProgressCard from '../components/DsaProgressCard';

const ProblemListPage = () => {
  const [problems, setProblems] = useState([]);
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedDifficulty, setSelectedDifficulty] = useState('');
  const [selectedTopic, setSelectedTopic] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('ALL'); // 'ALL' | 'SOLVED' | 'UNSOLVED'
  const [userProgress, setUserProgress] = useState(null);
  const [attemptingCount, setAttemptingCount] = useState(0);
  const isAuth = api.isAuthenticated();

  const difficulties = ['EASY', 'MEDIUM', 'HARD'];

  useEffect(() => {
    fetchProblems();
    fetchTopics();
    if (isAuth) {
      fetchProgress();
    }
  }, []);

  useEffect(() => {
    if (searchKeyword || selectedDifficulty || selectedTopic) {
      handleFilter();
    } else {
      fetchProblems();
    }
  }, [searchKeyword, selectedDifficulty, selectedTopic]);

  const fetchProblems = async () => {
    try {
      setLoading(true);
      const data = await api.getAllProblems();
      setProblems(data);
    } catch (error) {
      console.error('Error fetching problems:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchTopics = async () => {
    try {
      const data = await api.getAllTopics();
      setTopics(data);
    } catch (error) {
      console.error('Error fetching topics:', error);
    }
  };

  const fetchProgress = async () => {
    try {
      const progress = await api.getUserProgress();
      setUserProgress(progress);
      try {
        const history = await api.getHistory();
        if (Array.isArray(history)) {
          const solvedSet = new Set((progress?.solvedProblemIds || []).map(String));
          const attemptingSet = new Set(
            history
              .map((h) => h.problemId)
              .filter((id) => id != null && !solvedSet.has(String(id)))
          );
          setAttemptingCount(attemptingSet.size);
        }
      } catch (histErr) {
        console.warn('Could not fetch history for attempting count:', histErr);
      }
    } catch (error) {
      console.error('Error fetching user progress:', error);
    }
  };

  const handleFilter = async () => {
    try {
      setLoading(true);
      let data;
      if (searchKeyword) {
        data = await api.searchProblems(searchKeyword);
      } else if (selectedDifficulty && selectedTopic) {
        data = await api.filterProblems(selectedDifficulty, selectedTopic);
      } else if (selectedDifficulty) {
        data = await api.getProblemsByDifficulty(selectedDifficulty);
      } else if (selectedTopic) {
        data = await api.getProblemsByTopic(selectedTopic);
      } else {
        data = await api.getAllProblems();
      }
      setProblems(data);
    } catch (error) {
      console.error('Error filtering problems:', error);
    } finally {
      setLoading(false);
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

  const solvedProblemIds = userProgress?.solvedProblemIds || [];

  const displayedProblems = problems.filter((problem) => {
    const isSolved = solvedProblemIds.includes(problem.id);
    if (selectedStatus === 'SOLVED') return isSolved;
    if (selectedStatus === 'UNSOLVED') return !isSolved;
    return true;
  });

  return (
    <div className="min-h-screen bg-[#0b1120] text-slate-100">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(59,130,246,0.12),transparent_30%),radial-gradient(circle_at_top_right,rgba(16,185,129,0.08),transparent_25%),linear-gradient(to_bottom,rgba(15,23,42,0.95),rgba(2,6,23,1))] pointer-events-none" />

      {/* Top Header */}
      <header className="relative z-30 border-b border-slate-800/80 bg-slate-950/80 backdrop-blur-xl">
        <div className="mx-auto flex h-16 w-full max-w-7xl items-center justify-between gap-3 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center gap-3 group">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-sky-500/20 bg-sky-500/10 text-sky-300 shadow-lg shadow-sky-500/10">
              <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
                <path d="M16 18l6-6-6-6" strokeLinecap="round" strokeLinejoin="round" />
                <path d="M8 6l-6 6 6 6" strokeLinecap="round" strokeLinejoin="round" />
                <path d="M14 4L10 20" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-lg font-semibold tracking-tight text-slate-50">
                  CodeArena
                </h1>
                <span className="rounded-full border border-slate-700 bg-slate-900 px-2 py-0.5 text-[11px] font-medium uppercase tracking-[0.18em] text-slate-300">
                  DSA Library
                </span>
              </div>
              <p className="truncate text-xs font-medium text-cyan-400/90 tracking-wide">
                Built by Kashiling Bhusnar
              </p>
            </div>

            {/* Navigation pill */}
            <nav className="flex items-center gap-1.5 border-l border-slate-800 pl-3 ml-2">
              <span className="flex items-center gap-1.5 rounded-xl border border-cyan-400/70 bg-cyan-500/25 px-3 py-1.5 text-xs font-semibold text-white shadow-md shadow-cyan-500/20 ring-1 ring-cyan-400/30">
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
              </span>
            </nav>
          </div>

          <div className="flex items-center gap-3">
            <Link
              to="/editor"
              className="inline-flex items-center gap-2 rounded-xl border border-cyan-500/30 bg-cyan-500/10 px-3.5 py-1.5 text-xs sm:text-sm font-medium text-cyan-200 shadow-sm hover:border-cyan-400/50 hover:bg-cyan-500/20 transition-all"
            >
              <svg className="h-4 w-4 text-cyan-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M16 18l6-6-6-6" strokeLinecap="round" strokeLinejoin="round" />
                <path d="M8 6l-6 6 6 6" strokeLinecap="round" strokeLinejoin="round" />
                <path d="M14 4L10 20" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
              <span>Open Code Editor</span>
            </Link>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">Problem Library</h2>
          <p className="mt-0.5 text-xs font-medium text-cyan-400/90 tracking-wide">
            Built by Kashiling Bhusnar
          </p>
          <p className="mt-1 text-sm text-slate-400">Practice curated DSA problems with live multi-language test case evaluation</p>
        </div>

        {/* LeetCode-style DSA Solving Progress Card */}
        {isAuth && userProgress && (
          <DsaProgressCard
            userProgress={userProgress}
            problems={problems}
            attemptingCount={attemptingCount}
          />
        )}

        {/* Filters Section (including Feature 2: All / Solved / Unsolved) */}
        <div className="rounded-2xl border border-slate-800/80 bg-slate-900/60 p-4 sm:p-5 mb-6 backdrop-blur-xl shadow-xl shadow-black/20">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {/* Status Filter */}
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                Status
              </label>
              <div className="flex rounded-xl bg-slate-950/80 p-1 border border-slate-800">
                {[
                  { key: 'ALL', label: 'All' },
                  { key: 'SOLVED', label: 'Solved' },
                  { key: 'UNSOLVED', label: 'Unsolved' },
                ].map((item) => (
                  <button
                    key={item.key}
                    type="button"
                    onClick={() => setSelectedStatus(item.key)}
                    className={`flex-1 rounded-lg py-1.5 text-xs font-medium transition-all ${
                      selectedStatus === item.key
                        ? 'bg-gradient-to-r from-sky-500 to-cyan-400 text-slate-950 shadow-md font-semibold'
                        : 'text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    {item.label}
                  </button>
                ))}
              </div>
            </div>

            {/* Search */}
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                Search
              </label>
              <div className="relative">
                <input
                  type="text"
                  placeholder="Search by title..."
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  className="w-full rounded-xl border border-slate-800 bg-slate-950/80 px-3.5 py-2 text-sm text-slate-200 placeholder-slate-500 focus:border-cyan-500 focus:outline-none focus:ring-1 focus:ring-cyan-500"
                />
                {searchKeyword && (
                  <button
                    type="button"
                    onClick={() => setSearchKeyword('')}
                    className="absolute right-3 top-2.5 text-xs text-slate-500 hover:text-slate-300"
                  >
                    ✕
                  </button>
                )}
              </div>
            </div>

            {/* Difficulty */}
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                Difficulty
              </label>
              <select
                value={selectedDifficulty}
                onChange={(e) => setSelectedDifficulty(e.target.value)}
                className="w-full rounded-xl border border-slate-800 bg-slate-950/80 px-3 py-2 text-sm text-slate-200 focus:border-cyan-500 focus:outline-none focus:ring-1 focus:ring-cyan-500"
              >
                <option value="">All Difficulties</option>
                {difficulties.map((diff) => (
                  <option key={diff} value={diff} className="bg-slate-900 text-slate-200">
                    {diff}
                  </option>
                ))}
              </select>
            </div>

            {/* Topic */}
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                Topic
              </label>
              <select
                value={selectedTopic}
                onChange={(e) => setSelectedTopic(e.target.value)}
                className="w-full rounded-xl border border-slate-800 bg-slate-950/80 px-3 py-2 text-sm text-slate-200 focus:border-cyan-500 focus:outline-none focus:ring-1 focus:ring-cyan-500"
              >
                <option value="">All Topics</option>
                {topics.map((topic) => (
                  <option key={topic.id} value={topic.name} className="bg-slate-900 text-slate-200">
                    {topic.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* Problem List Table */}
        {loading ? (
          <div className="rounded-2xl border border-slate-800/80 bg-slate-900/40 p-12 text-center backdrop-blur-xl">
            <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-cyan-400"></div>
            <p className="mt-3 text-sm text-slate-400">Loading problems...</p>
          </div>
        ) : displayedProblems.length === 0 ? (
          <div className="rounded-2xl border border-slate-800/80 bg-slate-900/40 p-12 text-center backdrop-blur-xl">
            <p className="text-slate-400 text-base">No problems match your selected criteria.</p>
            {selectedStatus !== 'ALL' && (
              <button
                type="button"
                onClick={() => setSelectedStatus('ALL')}
                className="mt-3 text-sm text-cyan-400 hover:text-cyan-300 underline"
              >
                Reset status filter to All
              </button>
            )}
          </div>
        ) : (
          <div className="overflow-hidden rounded-2xl border border-slate-800/80 bg-slate-900/60 shadow-xl shadow-black/20 backdrop-blur-xl">
            <table className="min-w-full divide-y divide-slate-800">
              <thead className="bg-slate-950/60">
                <tr>
                  <th className="px-6 py-3.5 text-left text-xs font-semibold uppercase tracking-wider text-slate-400">
                    Status
                  </th>
                  <th className="px-6 py-3.5 text-left text-xs font-semibold uppercase tracking-wider text-slate-400">
                    Title
                  </th>
                  <th className="px-6 py-3.5 text-left text-xs font-semibold uppercase tracking-wider text-slate-400">
                    Difficulty
                  </th>
                  <th className="px-6 py-3.5 text-left text-xs font-semibold uppercase tracking-wider text-slate-400">
                    Topics
                  </th>
                  <th className="px-6 py-3.5 text-right text-xs font-semibold uppercase tracking-wider text-slate-400">
                    Action
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 bg-transparent">
                {displayedProblems.map((problem) => {
                  const isSolved = solvedProblemIds.includes(problem.id);
                  return (
                    <tr key={problem.id} className="transition-colors hover:bg-slate-800/40">
                      <td className="px-6 py-4 whitespace-nowrap">
                        {isSolved ? (
                          <span className="inline-flex items-center gap-1.5 rounded-full border border-emerald-500/30 bg-emerald-500/15 px-2.5 py-0.5 text-xs font-semibold text-emerald-300">
                            <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                              <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                            </svg>
                            <span>Solved</span>
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1.5 text-xs text-slate-500">
                            <span className="h-3 w-3 rounded-full border border-slate-700 bg-slate-900/60 inline-block" />
                            <span>Unsolved</span>
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <Link
                          to={`/problems/${problem.id}`}
                          className="font-medium text-slate-200 hover:text-cyan-400 transition-colors"
                        >
                          {problem.id}. {problem.title}
                        </Link>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span
                          className={`inline-block px-2.5 py-1 rounded-full text-xs font-semibold border ${getDifficultyColor(
                            problem.difficulty
                          )}`}
                        >
                          {problem.difficulty}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex flex-wrap gap-1.5">
                          {problem.topics && problem.topics.length > 0 ? (
                            problem.topics.slice(0, 3).map((topic) => (
                              <span
                                key={topic.id}
                                className="rounded-md border border-slate-700 bg-slate-800/80 px-2 py-0.5 text-xs font-medium text-sky-300"
                              >
                                {topic.name}
                              </span>
                            ))
                          ) : (
                            <span className="text-slate-500 text-xs">No topics</span>
                          )}
                          {problem.topics && problem.topics.length > 3 && (
                            <span className="text-slate-500 text-xs">
                              +{problem.topics.length - 3}
                            </span>
                          )}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right">
                        <Link
                          to={`/editor?problemId=${problem.id}`}
                          className="inline-flex items-center gap-1.5 rounded-lg border border-cyan-500/30 bg-cyan-500/10 px-3 py-1.5 text-xs font-medium text-cyan-300 hover:border-cyan-400/50 hover:bg-cyan-500/20 transition-all"
                        >
                          <span>Solve</span>
                          <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                            <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
                          </svg>
                        </Link>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProblemListPage;
