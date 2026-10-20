import React from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import EditorPage from './pages/EditorPage';
import ProblemListPage from './pages/ProblemListPage';
import ProblemDetailPage from './pages/ProblemDetailPage';
import CareerTracksPage from './pages/CareerTracksPage';
import TrackRoadmapPage from './pages/TrackRoadmapPage';
import TopicReaderPage from './pages/TopicReaderPage';
import ProtectedRoute from './routes/ProtectedRoute';
import { api } from './services/api';

/**
 * RootRoute - When user opens localhost:3000 (/):
 * - If not authenticated: opens /login immediately.
 * - If authenticated: redirects to the career learning dashboard (/learning).
 */
const RootRoute = () => {
  return <Navigate to={api.isAuthenticated() ? '/learning' : '/login'} replace />;
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={<RootRoute />}
        />
        <Route
          path="/login"
          element={<LoginPage />}
        />
        <Route
          path="/problems"
          element={<ProblemListPage />}
        />
        <Route
          path="/problems/:id"
          element={<ProblemDetailPage />}
        />
        <Route
          path="/learning"
          element={<CareerTracksPage />}
        />
        <Route
          path="/learning/:trackSlug"
          element={<TrackRoadmapPage />}
        />
        <Route
          path="/learning/:trackSlug/:topicSlug"
          element={<TopicReaderPage />}
        />
        <Route
          path="/editor"
          element={
            <ProtectedRoute>
              <EditorPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="*"
          element={<RootRoute />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
