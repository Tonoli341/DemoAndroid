import { FormEvent, useEffect, useState } from 'react'
import { fetchDashboard, fetchLogs, updateConfig, User } from '../components/api'
import { useNavigate } from 'react-router-dom'

interface Props {
  user: User
  onLogout: () => void
}

export default function AdminSettings({ user, onLogout }: Props) {
  const [alertHour, setAlertHour] = useState(17)
  const [recipients, setRecipients] = useState('responsabile@example.com')
  const [logs, setLogs] = useState<Array<{ id: string; timestamp: string; description: string }>>([])
  const [stats, setStats] = useState<{ pendingApproval: any[]; anomalies: any[]; recent: any[] }>()
  const [message, setMessage] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    fetchDashboard().then(setStats)
    fetchLogs().then(setLogs)
  }, [])

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    await updateConfig({ dailyAlertHour: alertHour, alertRecipients: recipients.split(',').map((s) => s.trim()) }, user.id)
    setMessage('Configurazione salvata')
  }

  return (
    <div className="container" style={{ paddingTop: 24 }}>
      <header className="flex-between" style={{ marginBottom: 12 }}>
        <div>
          <div style={{ color: '#6b7280', fontSize: 14 }}>Amministratore</div>
          <h2 style={{ margin: 0 }}>{user.fullName}</h2>
        </div>
        <div className="flex-between" style={{ gap: 8 }}>
          <button className="button secondary" style={{ width: 'auto' }} onClick={() => navigate('/manager')}>Vista responsabile</button>
          <button className="button secondary" style={{ width: 'auto' }} onClick={onLogout}>Esci</button>
        </div>
      </header>
      <div className="grid" style={{ gap: 16 }}>
        <section className="card grid" style={{ gap: 8 }}>
          <h3 className="section-title">Parametri</h3>
          <form className="grid" style={{ gap: 8 }} onSubmit={handleSubmit}>
            <label>Ora alert mancata compilazione
              <input className="input" type="number" value={alertHour} min={0} max={23} onChange={(e) => setAlertHour(parseInt(e.target.value) || 0)} />
            </label>
            <label>Destinatari alert (comma)
              <input className="input" value={recipients} onChange={(e) => setRecipients(e.target.value)} />
            </label>
            <button className="button primary" type="submit">Salva</button>
          </form>
          {message && <div className="badge confirmed">{message}</div>}
        </section>

        <section className="card grid" style={{ gap: 8 }}>
          <div className="flex-between">
            <h3 className="section-title">Statistiche rapide</h3>
            <span style={{ color: '#6b7280' }}>Controllo stato</span>
          </div>
          <div className="grid" style={{ gap: 4 }}>
            <div>In attesa approvazione: {stats?.pendingApproval.length ?? 0}</div>
            <div>Anomalie: {stats?.anomalies.length ?? 0}</div>
            <div>Ultimi turni: {stats?.recent.length ?? 0}</div>
          </div>
        </section>

        <section className="card grid" style={{ gap: 8 }}>
          <div className="flex-between">
            <h3 className="section-title">Log modifiche</h3>
            <span style={{ color: '#6b7280' }}>Ultimi eventi</span>
          </div>
          {logs.map((log) => (
            <div key={log.id} style={{ borderBottom: '1px solid #e5e7eb', paddingBottom: 6 }}>
              <div style={{ fontWeight: 700 }}>{log.timestamp}</div>
              <div style={{ fontSize: 14 }}>{log.description}</div>
            </div>
          ))}
          {logs.length === 0 && <div style={{ color: '#6b7280' }}>Nessun log ancora disponibile</div>}
        </section>
      </div>
    </div>
  )
}
