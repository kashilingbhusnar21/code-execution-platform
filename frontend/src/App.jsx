import React from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import EditorPage from './pages/EditorPage';
import ProtectedRoute from './routes/ProtectedRoute';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={<Navigate to={localStorage.getItem('token') ? '/editor' : '/login'} replace />}
        />
        <Route
          path="/login"
          element={<LoginPage />}
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
          element={<Navigate to={localStorage.getItem('token') ? '/editor' : '/login'} replace />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
