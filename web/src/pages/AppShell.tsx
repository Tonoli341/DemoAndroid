import { useEffect, useState } from 'react'
import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import LoginPage from './LoginPage'
import OperatorWizard from './OperatorWizard'
import WeekHistory from './WeekHistory'
import ManagerDashboard from './ManagerDashboard'
import AdminSettings from './AdminSettings'
import { User } from '../components/api'

export default function AppShell() {
  const [user, setUser] = useState<User | null>(null)
  const navigate = useNavigate()

  useEffect(() => {
    if (!user) navigate('/')
  }, [user, navigate])

  return (
    <Routes>
      <Route path="/" element={<LoginPage onLogin={(u) => setUser(u)} />} />
      <Route
        path="/operator"
        element={user ? <OperatorWizard user={user} onLogout={() => setUser(null)} /> : <Navigate to="/" replace />}
      />
      <Route
        path="/history"
        element={user ? <WeekHistory user={user} onLogout={() => setUser(null)} /> : <Navigate to="/" replace />}
      />
      <Route
        path="/manager"
        element={user ? <ManagerDashboard user={user} onLogout={() => setUser(null)} /> : <Navigate to="/" replace />}
      />
      <Route
        path="/admin"
        element={user ? <AdminSettings user={user} onLogout={() => setUser(null)} /> : <Navigate to="/" replace />}
      />
    </Routes>
  )
}
