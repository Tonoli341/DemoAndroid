import { FormEvent, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { fetchOptions, fetchWeek, submitShift, WorkShift, User } from '../components/api'

interface Props {
  user: User
  onLogout: () => void
}

export default function OperatorWizard({ user, onLogout }: Props) {
  const [step, setStep] = useState(1)
  const [startTime, setStartTime] = useState('08:00')
  const [endTime, setEndTime] = useState('17:00')
  const [breakMinutes, setBreakMinutes] = useState(60)
  const [slices, setSlices] = useState([
    { projectId: '', costCenterId: '', startTime: '08:00', endTime: '12:00', notes: '' }
  ])
  const [options, setOptions] = useState<{ projects: any[]; costCenters: any[] }>({ projects: [], costCenters: [] })
  const [statusMessage, setStatusMessage] = useState('')
  const [history, setHistory] = useState<WorkShift[]>([])
  const navigate = useNavigate()

  useEffect(() => {
    fetchOptions().then(setOptions)
    fetchWeek(user.id).then(setHistory)
  }, [user.id])

  const addSlice = () => {
    setSlices([...slices, { projectId: '', costCenterId: '', startTime: '13:00', endTime: '17:00', notes: '' }])
  }

  const updateSlice = (idx: number, field: string, value: string) => {
    setSlices((prev) => prev.map((s, i) => (i === idx ? { ...s, [field]: value } : s)))
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    const payload = {
      userId: user.id,
      date: new Date().toISOString().slice(0, 10),
      startTime,
      endTime,
      breakMinutes,
      slices,
      submitAsConfirmed: true
    }
    const created = await submitShift(payload)
    setStatusMessage(created.anomalies.length ? created.anomalies.join(' | ') : 'Giornata salvata')
    setHistory([created, ...history])
    setStep(1)
  }

  return (
    <div className="container" style={{ paddingTop: 24 }}>
      <header className="flex-between" style={{ marginBottom: 12 }}>
        <div>
          <div style={{ fontSize: 14, color: '#6b7280' }}>Operatore</div>
          <h2 style={{ margin: 0 }}>{user.fullName}</h2>
        </div>
        <div className="flex-between" style={{ gap: 8 }}>
          <button className="button secondary" style={{ width: 'auto' }} onClick={() => navigate('/history')}>Storico</button>
          <button className="button secondary" style={{ width: 'auto' }} onClick={onLogout}>Esci</button>
        </div>
      </header>
      <div className="grid" style={{ gap: 16 }}>
        <div className="card grid" style={{ gap: 12 }}>
          <div className="flex-between">
            <h3 className="section-title">Step {step} / 2</h3>
            <div className="badge confirmed">Turno odierno</div>
          </div>
          {step === 1 && (
            <form className="grid" style={{ gap: 12 }} onSubmit={() => setStep(2)}>
              <label>Ora entrata<input className="input" value={startTime} onChange={(e) => setStartTime(e.target.value)} required /></label>
              <label>Ora uscita<input className="input" value={endTime} onChange={(e) => setEndTime(e.target.value)} required /></label>
              <label>Pausa (minuti)<input className="input" type="number" value={breakMinutes} onChange={(e) => setBreakMinutes(parseInt(e.target.value) || 0)} required /></label>
              <button className="button primary" type="submit">Avanti</button>
            </form>
          )}
          {step === 2 && (
            <form className="grid" style={{ gap: 12 }} onSubmit={handleSubmit}>
              {slices.map((slice, idx) => (
                <div key={idx} className="card" style={{ background: '#f9fafb' }}>
                  <div className="grid" style={{ gap: 8 }}>
                    <label>Commessa
                      <select className="input" value={slice.projectId} onChange={(e) => updateSlice(idx, 'projectId', e.target.value)} required>
                        <option value="">Seleziona</option>
                        {options.projects.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
                      </select>
                    </label>
                    <label>Centro di costo
                      <select className="input" value={slice.costCenterId} onChange={(e) => updateSlice(idx, 'costCenterId', e.target.value)} required>
                        <option value="">Seleziona</option>
                        {options.costCenters.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
                      </select>
                    </label>
                    <div className="flex-between">
                      <label style={{ width: '50%' }}>Dalle
                        <input className="input" value={slice.startTime} onChange={(e) => updateSlice(idx, 'startTime', e.target.value)} required />
                      </label>
                      <label style={{ width: '50%' }}>Alle
                        <input className="input" value={slice.endTime} onChange={(e) => updateSlice(idx, 'endTime', e.target.value)} required />
                      </label>
                    </div>
                    <label>Note facoltative
                      <input className="input" value={slice.notes} onChange={(e) => updateSlice(idx, 'notes', e.target.value)} />
                    </label>
                  </div>
                </div>
              ))}
              <div className="flex-between">
                <button className="button secondary" type="button" onClick={() => setStep(1)}>Indietro</button>
                <button className="button secondary" type="button" onClick={addSlice}>Aggiungi fascia</button>
                <button className="button primary" type="submit">Conferma giornata</button>
              </div>
            </form>
          )}
          {statusMessage && <div className="badge alert">{statusMessage}</div>}
        </div>
        <div className="card">
          <div className="flex-between" style={{ marginBottom: 8 }}>
            <h3 className="section-title">Settimana</h3>
            <span style={{ color: '#6b7280' }}>Ultimi invii</span>
          </div>
          <div className="grid">
            {history.map((h) => (
              <div key={h.id} className="flex-between" style={{ borderBottom: '1px solid #e5e7eb', paddingBottom: 8 }}>
                <div>
                  <div style={{ fontWeight: 700 }}>{h.date}</div>
                  {h.anomalies.length > 0 && <div className="badge alert">{h.anomalies.join(' | ')}</div>}
                </div>
                <span className={`badge ${h.status.toLowerCase()}`}>{h.status}</span>
              </div>
            ))}
            {history.length === 0 && <div style={{ color: '#6b7280' }}>Nessuna giornata registrata</div>}
          </div>
        </div>
      </div>
    </div>
  )
}
