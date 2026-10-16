import { useState } from 'react'
import { getCurrentUser, setApiToken } from '../../services/incidentsApi.js'
import '../incidents/incident-pages.css'

const frontendRoleByApiRole = {
  CITIZEN: 'citizen',
  RESCUE_TEAM: 'rescue',
  ADMIN: 'admin',
}

function LocalTokenLogin({ onAuthenticated }) {
  const [token, setToken] = useState('')
  const [error, setError] = useState('')
  const [checking, setChecking] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setChecking(true)
    setError('')
    try {
      const identity = await getCurrentUser(token.trim())
      const role = frontendRoleByApiRole[identity.role]
      if (!role) throw new Error('The backend returned an unsupported role.')
      setApiToken(token.trim())
      setToken('')
      onAuthenticated(role)
    } catch (requestError) {
      setApiToken(null)
      setError(requestError.message || 'Unable to authenticate with the backend.')
    } finally {
      setChecking(false)
    }
  }

  return (
    <main className="main-area auth-entry">
      <section className="page-content" aria-labelledby="auth-title">
        <p className="eyebrow">LOCAL DEVELOPMENT ONLY</p>
        <h1 id="auth-title">Sign in to Sahayak AI</h1>
        <p className="page-description">
          Enter a locally configured development access token. The backend determines your role;
          this form does not accept a role selection. This is not OTP login or production authentication.
        </p>
        <form className="incident-form auth-form" onSubmit={handleSubmit}>
          <div className="form-field">
            <label htmlFor="dev-access-token">Development access token</label>
            <input
              id="dev-access-token"
              type="password"
              autoComplete="off"
              value={token}
              onChange={(event) => setToken(event.target.value)}
              required
              disabled={checking}
            />
          </div>
          {error && <p className="auth-error" role="alert">{error}</p>}
          <div className="form-actions">
            <button className="primary-button" type="submit" disabled={checking || !token.trim()}>
              {checking ? 'Checking token…' : 'Continue'}
            </button>
          </div>
        </form>
      </section>
    </main>
  )
}

export default LocalTokenLogin
