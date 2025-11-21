import { FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login, Role, User } from '../components/api'

interface Props {
  onLogin: (user: User) => void
}

const roleRedirect: Record<Role, string> = {
  OPERATOR: '/operator',
  MANAGER: '/manager',
  ADMIN: '/admin'
}

export default function LoginPage({ onLogin }: Props) {
  const [username, setUsername] = useState('')
  const [pin, setPin] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      const user = await login(username, pin)
      onLogin(user)
      navigate(roleRedirect[user.role])
    } catch (err) {
      setError('Credenziali non valide')
    }
  }

  return (
    <div className="container" style={{ maxWidth: 420, paddingTop: 80 }}>
      <div className="card grid" style={{ gap: 16 }}>
        <div>
          <h1 style={{ margin: '0 0 12px' }}>Accesso</h1>
          <p style={{ margin: 0, color: '#4b5563' }}>Inserisci username e PIN</p>
        </div>
        {error && <div className="badge alert">{error}</div>}
        <form className="grid" style={{ gap: 12 }} onSubmit={handleSubmit}>
          <label>
            Username
            <input className="input" value={username} onChange={(e) => setUsername(e.target.value)} required />
          </label>
          <label>
            PIN
            <input className="input" type="password" value={pin} onChange={(e) => setPin(e.target.value)} required />
          </label>
          <button className="button primary" type="submit">Entra</button>
        </form>
      </div>
    </div>
  )
}
