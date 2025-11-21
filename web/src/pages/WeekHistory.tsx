import { useEffect, useState } from 'react'
import { fetchWeek, WorkShift, User } from '../components/api'
import { useNavigate } from 'react-router-dom'

interface Props {
  user: User
  onLogout: () => void
}

export default function WeekHistory({ user, onLogout }: Props) {
  const [items, setItems] = useState<WorkShift[]>([])
  const navigate = useNavigate()

  useEffect(() => {
    fetchWeek(user.id).then(setItems)
  }, [user.id])

  return (
    <div className="container" style={{ paddingTop: 24 }}>
      <header className="flex-between" style={{ marginBottom: 12 }}>
        <h2 style={{ margin: 0 }}>Storico settimana</h2>
        <div className="flex-between" style={{ gap: 8 }}>
          <button className="button secondary" style={{ width: 'auto' }} onClick={() => navigate('/operator')}>Torna</button>
          <button className="button secondary" style={{ width: 'auto' }} onClick={onLogout}>Esci</button>
        </div>
      </header>
      <div className="card grid" style={{ gap: 8 }}>
        {items.map((item) => (
          <div key={item.id} className="grid" style={{ borderBottom: '1px solid #e5e7eb', paddingBottom: 8 }}>
            <div className="flex-between">
              <div>
                <div style={{ fontWeight: 700 }}>{item.date}</div>
                <small>Turno: {item.startTime} - {item.endTime} (pausa {item.breakMinutes}m)</small>
              </div>
              <span className={`badge ${item.status.toLowerCase()}`}>{item.status}</span>
            </div>
            <div className="grid" style={{ gap: 4 }}>
              {item.slices.map((s) => (
                <div key={s.id} style={{ fontSize: 14 }}>
                  {s.startTime} - {s.endTime} → {s.projectId} / {s.costCenterId} {s.notes && `(${s.notes})`}
                </div>
              ))}
            </div>
            {item.anomalies.length > 0 && <div className="badge alert">{item.anomalies.join(' | ')}</div>}
          </div>
        ))}
        {items.length === 0 && <div style={{ color: '#6b7280' }}>Nessun turno inviato.</div>}
      </div>
    </div>
  )
}
