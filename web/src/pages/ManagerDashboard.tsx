import { useEffect, useState } from 'react'
import { approveShift, fetchDashboard, WorkShift, User } from '../components/api'
import { useNavigate } from 'react-router-dom'

interface Props {
  user: User
  onLogout: () => void
}

export default function ManagerDashboard({ user, onLogout }: Props) {
  const [data, setData] = useState<{ pendingApproval: WorkShift[]; anomalies: WorkShift[]; recent: WorkShift[] }>()
  const navigate = useNavigate()

  const load = () => fetchDashboard().then(setData)

  useEffect(() => {
    load()
  }, [])

  const approve = async (id: string) => {
    await approveShift(id, user.id)
    load()
  }

  return (
    <div className="container" style={{ paddingTop: 24 }}>
      <header className="flex-between" style={{ marginBottom: 12 }}>
        <div>
          <div style={{ color: '#6b7280', fontSize: 14 }}>Responsabile</div>
          <h2 style={{ margin: 0 }}>{user.fullName}</h2>
        </div>
        <div className="flex-between" style={{ gap: 8 }}>
          <button className="button secondary" style={{ width: 'auto' }} onClick={() => navigate('/operator')}>Vista operatore</button>
          <button className="button secondary" style={{ width: 'auto' }} onClick={onLogout}>Esci</button>
        </div>
      </header>
      <div className="grid" style={{ gap: 16 }}>
        <section className="card grid" style={{ gap: 8 }}>
          <div className="flex-between">
            <h3 className="section-title">In attesa di approvazione</h3>
            <span style={{ color: '#6b7280' }}>{data?.pendingApproval.length ?? 0} turni</span>
          </div>
          {data?.pendingApproval.map((item) => (
            <div key={item.id} className="grid" style={{ borderBottom: '1px solid #e5e7eb', paddingBottom: 8 }}>
              <div className="flex-between">
                <div>
                  <div style={{ fontWeight: 700 }}>{item.date}</div>
                  <small>{item.startTime} - {item.endTime} | utente {item.userId}</small>
                </div>
                <button className="button primary" style={{ width: 'auto' }} onClick={() => approve(item.id)}>Approva</button>
              </div>
              {item.anomalies.length > 0 && <div className="badge alert">{item.anomalies.join(' | ')}</div>}
            </div>
          ))}
          {(data?.pendingApproval?.length ?? 0) === 0 && <div style={{ color: '#6b7280' }}>Nulla da approvare</div>}
        </section>

        <section className="card grid" style={{ gap: 8 }}>
          <div className="flex-between">
            <h3 className="section-title">Anomalie</h3>
            <span style={{ color: '#6b7280' }}>Controlli automatici</span>
          </div>
          {data?.anomalies.map((item) => (
            <div key={item.id} className="grid" style={{ borderBottom: '1px solid #e5e7eb', paddingBottom: 8 }}>
              <div>{item.date} • utente {item.userId}</div>
              <div className="badge alert">{item.anomalies.join(' | ')}</div>
            </div>
          ))}
          {(data?.anomalies?.length ?? 0) === 0 && <div style={{ color: '#6b7280' }}>Nessuna anomalia recente</div>}
        </section>

        <section className="card grid" style={{ gap: 8 }}>
          <div className="flex-between">
            <h3 className="section-title">Ultimi turni</h3>
            <span style={{ color: '#6b7280' }}>Per monitoraggio rapido</span>
          </div>
          {data?.recent.map((item) => (
            <div key={item.id} className="flex-between" style={{ borderBottom: '1px solid #e5e7eb', paddingBottom: 8 }}>
              <div>
                <div style={{ fontWeight: 700 }}>{item.date}</div>
                <small>{item.userId}</small>
              </div>
              <span className={`badge ${item.status.toLowerCase()}`}>{item.status}</span>
            </div>
          ))}
        </section>
      </div>
    </div>
  )
}
