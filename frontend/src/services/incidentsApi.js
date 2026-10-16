const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
).replace(/\/+$/, '')

let apiToken = null

export function setApiToken(token) {
  apiToken = token
}

async function request(path, options = {}) {
  const { token, ...fetchOptions } = options
  const headers = new Headers(options.headers ?? {})
  headers.set('Accept', 'application/json')
  if (fetchOptions.body) headers.set('Content-Type', 'application/json')
  const activeToken = token ?? apiToken
  if (activeToken) headers.set('Authorization', `Bearer ${activeToken}`)

  const response = await fetch(`${API_BASE_URL}${path}`, { ...fetchOptions, headers })
  const payload = await response.json().catch(() => null)

  if (!response.ok) {
    const error = new Error(payload?.message || `Request failed with status ${response.status}`)
    error.status = response.status
    error.fields = payload?.fields ?? {}
    throw error
  }

  return payload
}

export function getCurrentUser(token) {
  return request('/auth/me', { token })
}

export function getIncidents({ signal } = {}) {
  return request('/incidents', { signal })
}

export function getIncidentById(id, { signal } = {}) {
  return request(`/incidents/${encodeURIComponent(id)}`, { signal })
}

export function createIncident(incident, { signal } = {}) {
  return request('/incidents', {
    method: 'POST',
    body: JSON.stringify(incident),
    signal,
  })
}

export function updateIncidentStatus(id, status, { signal } = {}) {
  return request(`/incidents/${encodeURIComponent(id)}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
    signal,
  })
}
