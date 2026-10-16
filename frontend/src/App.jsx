import './App.css'
import { useState } from 'react'
import { EmptyState, StatusBadge } from './components/feedback/index.js'
import { roleNavigation, roleOptions } from './navigation/roleNavigation.js'

function App() {
  const [role, setRole] = useState('citizen')
  const [pageId, setPageId] = useState(roleNavigation.citizen.items[0].id)
  const currentRole = roleNavigation[role]
  const currentPage = currentRole.items.find((item) => item.id === pageId)

  function handleRoleChange(event) {
    const nextRole = event.target.value
    setRole(nextRole)
    setPageId(roleNavigation[nextRole].items[0].id)
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
          <label htmlFor="role-preview-select">Role preview <span>Development only</span></label>
          <select id="role-preview-select" value={role} onChange={handleRoleChange}>
            {roleOptions.map((option) => (
              <option key={option.value} value={option.value}>{option.label}</option>
            ))}
          </select>
          <p>Preview only. This does not sign you in or enforce permissions.</p>
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

          <EmptyState
            title="Not connected yet"
            description={currentPage.placeholder}
          />
        </section>
      </main>
    </div>
  )
}

export default App
