import './App.css'
import { useState } from 'react'
import { EmptyState, StatusBadge } from './components/feedback/index.js'
import IncidentManagement from './features/incidents/IncidentManagement.jsx'
import IncidentReportForm from './features/incidents/IncidentReportForm.jsx'
import { roleNavigation } from './navigation/roleNavigation.js'
import { setApiToken } from './services/incidentsApi.js'
import LocalTokenLogin from './features/auth/LocalTokenLogin.jsx'

function App() {
  const [role, setRole] = useState(null)
  const [pageId, setPageId] = useState(roleNavigation.citizen.items[0].id)
  if (!role) {
    return <LocalTokenLogin onAuthenticated={(authenticatedRole) => {
      setRole(authenticatedRole)
      setPageId(roleNavigation[authenticatedRole].items[0].id)
    }} />
  }
  const currentRole = roleNavigation[role]
  const currentPage = currentRole.items.find((item) => item.id === pageId)

  function renderPage() {
    if (role === 'citizen' && pageId === 'report-incident') return <IncidentReportForm />
    if (role === 'admin' && ['control-center', 'incident-management'].includes(pageId)) {
      return <IncidentManagement audience="admin" />
    }
    if (role === 'citizen' && pageId === 'my-reports') {
      return <IncidentManagement audience="citizen" />
    }
    if (role === 'rescue' && pageId === 'assigned-incidents') {
      return <IncidentManagement audience="rescue" />
    }
    return <EmptyState title="Not connected yet" description={currentPage.placeholder} />
  }

  function handleSignOut() {
    setApiToken(null)
    setRole(null)
    setPageId(roleNavigation.citizen.items[0].id)
  }

  return (
    <div className="app-shell">
      <aside className="sidebar" aria-label="Sahayak AI navigation">
        <a className="brand" href="#workspace" aria-label="Sahayak AI workspace">
          <span className="brand-mark" aria-hidden="true">S</span>
          <span className="brand-name">Sahayak <span>AI</span></span>
        </a>

        <div className="role-context">{currentRole.label}</div>

        <nav className="sidebar-nav" aria-label={`${currentRole.label} navigation`}>
          {currentRole.items.map((item) => (
            <button
              className={`nav-link${currentPage.id === item.id ? ' nav-link-active' : ''}`}
              key={item.id}
              type="button"
              aria-current={currentPage.id === item.id ? 'page' : undefined}
              onClick={() => setPageId(item.id)}
            >
              <span className="nav-indicator" aria-hidden="true" />
              <span>{item.label}</span>
            </button>
          ))}
        </nav>

        <div className="role-preview">
          <p>Role verified by backend: <strong>{currentRole.label}</strong></p>
          <button className="secondary-button" type="button" onClick={handleSignOut}>Sign out</button>
        </div>
      </aside>

      <main className="main-area" id="workspace" tabIndex="-1">
        <header className="topbar">
          <p className="topbar-context">
            Sahayak AI <span aria-hidden="true">/</span> {currentRole.label}
          </p>
        </header>

        <section className="page-content" aria-labelledby="page-title">
          <div className="page-heading">
            <p className="eyebrow">{currentRole.label.toUpperCase()}</p>
            <div className="page-title-row">
              <h1 id="page-title">{currentPage.title}</h1>
              <StatusBadge
                status={currentPage.status ?? 'Not connected'}
                tone={currentPage.tone ?? 'neutral'}
              />
            </div>
            <p className="page-description">{currentPage.description}</p>
          </div>

          {renderPage()}
        </section>
      </main>
    </div>
  )
}

export default App
