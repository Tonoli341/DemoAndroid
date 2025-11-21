import axios from 'axios'

export type Role = 'OPERATOR' | 'MANAGER' | 'ADMIN'

export interface User {
  id: string
  username: string
  fullName: string
  role: Role
}

export interface Project { id: string; code: string; name: string }
export interface CostCenter { id: string; name: string; location: string }
export interface TimeSliceInput {
  projectId: string
  costCenterId: string
  startTime: string
  endTime: string
  notes?: string
}
export interface ShiftSubmission {
  userId: string
  date: string
  startTime: string
  endTime: string
  breakMinutes: number
  slices: TimeSliceInput[]
  submitAsConfirmed?: boolean
}

export interface WorkShift {
  id: string
  userId: string
  date: string
  startTime: string
  endTime: string
  breakMinutes: number
  slices: Array<TimeSliceInput & { id: string; workShiftId: string }>
  status: 'DRAFT' | 'CONFIRMED' | 'APPROVED'
  anomalies: string[]
  approverId?: string | null
  lastUpdatedBy?: string | null
}

export interface OptionsResponse {
  projects: Project[]
  costCenters: CostCenter[]
  config: { dailyAlertHour: number; alertRecipients: string[] }
}

const client = axios.create({ baseURL: '/api' })

export async function login(username: string, pin: string): Promise<User> {
  const res = await client.post('/auth/login', { username, pin })
  return res.data.user
}

export async function fetchOptions(): Promise<OptionsResponse> {
  const res = await client.get('/options')
  return res.data
}

export async function submitShift(payload: ShiftSubmission) {
  const res = await client.post('/workdays', payload)
  return res.data as WorkShift
}

export async function fetchWeek(userId: string) {
  const res = await client.get('/workdays/week', { params: { userId } })
  return (res.data.days as WorkShift[])
}

export async function confirmShift(id: string, actorId: string) {
  const res = await client.put(`/workdays/${id}/confirm`, null, { params: { actorId } })
  return res.data as WorkShift
}

export async function approveShift(id: string, actorId: string) {
  const res = await client.put(`/workdays/${id}/approve`, null, { params: { actorId } })
  return res.data as WorkShift
}

export async function fetchDashboard() {
  const res = await client.get('/dashboard')
  return res.data as { pendingApproval: WorkShift[]; anomalies: WorkShift[]; recent: WorkShift[] }
}

export async function fetchLogs() {
  const res = await client.get('/logs')
  return res.data as Array<{ id: string; timestamp: string; action: string; actorId: string; targetId: string; description: string }>
}

export async function updateConfig(config: { dailyAlertHour: number; alertRecipients: string[] }, actorId: string) {
  const res = await client.post('/config', config, { params: { actorId } })
  return res.data
}
