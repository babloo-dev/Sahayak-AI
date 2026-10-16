import { useCallback, useEffect, useState } from 'react'
import { EmptyState, ErrorState, LoadingState, StatusBadge } from '../../components/feedback/index.js'
import { getIncidentById, getIncidents, updateIncidentStatus } from '../../services/incidentsApi.js'
import './incident-pages.css'

function statusTone(status) {
  if (status === 'RESCUE_COMPLETED') return 'success'
  if (status === 'SUBMITTED' || status === 'REPORTED') return 'warning'
  if (status === 'RESCUE_OPERATION_STARTED') return 'info'
  if (status === 'TEAM_ASSIGNED' || status === 'TEAM_ON_THE_WAY') return 'info'
  return 'neutral'
}

const nextStatus = {
  TEAM_ASSIGNED: 'TEAM_ON_THE_WAY',
  TEAM_ON_THE_WAY: 'RESCUE_OPERATION_STARTED',
  RESCUE_OPERATION_STARTED: 'RESCUE_COMPLETED',
}

function IncidentManagement({ audience = 'admin' }) {
  const [incidents, setIncidents] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState(null)
  const [listAttempt, setListAttempt] = useState(0)
  const [selectedId, setSelectedId] = useState(null)
  const [detail, setDetail] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [detailError, setDetailError] = useState(null)
  const [detailAttempt, setDetailAttempt] = useState(0)
  const [statusUpdating, setStatusUpdating] = useState(false)
  const [statusError, setStatusError] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    getIncidents({ signal: controller.signal })
      .then(setIncidents)
      .catch((error) => {
        if (error.name !== 'AbortError') setListError(error)
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false)
      })
    return () => controller.abort()
  }, [listAttempt])

  useEffect(() => {
    if (selectedId === null) return undefined
    const controller = new AbortController()
    getIncidentById(selectedId, { signal: controller.signal })
      .then(setDetail)
      .catch((error) => {
        if (error.name !== 'AbortError') setDetailError(error)
      })
      .finally(() => {
        if (!controller.signal.aborted) setDetailLoading(false)
      })
    return () => controller.abort()
  }, [selectedId, detailAttempt])

  const refreshList = useCallback(() => {
    setLoading(true)
    setListError(null)
    setListAttempt((attempt) => attempt + 1)
  }, [])
  const selectIncident = useCallback((id) => {
    setDetail(null)
    setDetailError(null)
    setDetailLoading(true)
    setSelectedId(id)
  }, [])
  const retryDetail = useCallback(() => {
    setDetailLoading(true)
    setDetailError(null)
    setDetailAttempt((attempt) => attempt + 1)
  }, [])

  async function advanceStatus() {
    const targetStatus = nextStatus[detail?.status]
    if (!targetStatus || selectedId === null) return
    setStatusUpdating(true)
    setStatusError(null)
    try {
      const updated = await updateIncidentStatus(selectedId, targetStatus)
      setDetail(updated)
      setListAttempt((attempt) => attempt + 1)
    } catch (error) {
      setStatusError(error)
    } finally {
      setStatusUpdating(false)
    }
  }

  const audienceText = audience === 'citizen'
    ? 'The backend returns only reports owned by your authenticated account.'
    : audience === 'rescue'
      ? 'The backend returns only incidents assigned to your authenticated rescue team.'
      : 'The backend returns operational incident records for the authenticated Control Center role.'

  return (
    <div className="incident-workspace">
      <p className="integration-note" role="note">
        {audienceText} Records and permissions are enforced by the backend.
      </p>

      <section aria-labelledby="incident-list-title">
        <div className="incident-section-heading">
          <div>
            <h2 id="incident-list-title">{audience === 'citizen' ? 'My reports' : audience === 'rescue' ? 'Assigned incidents' : 'Incidents'}</h2>
            <p>Live records from the configured backend.</p>
          </div>
          <button className="secondary-button" type="button" onClick={refreshList} disabled={loading}>
            Refresh list
          </button>
        </div>

        {loading && <LoadingState label="Loading incidents" description="Retrieving records from the backend." />}
        {listError && (
          <ErrorState
            title="Unable to load incidents"
            message={listError.message}
            onRetry={refreshList}
          />
        )}
        {!loading && !listError && incidents.length === 0 && (
          <EmptyState title="No incidents returned" description="The backend currently has no incident records." />
        )}
        {!loading && !listError && incidents.length > 0 && (
          <div className="incident-list" aria-label="Incidents returned by the backend">
            {incidents.map((incident) => (
              <article className="incident-card" key={incident.id}>
                <div className="incident-card-heading">
                  <div>
                    <p className="incident-reference">Incident #{incident.id}</p>
                    <h3>{incident.incidentType}</h3>
                  </div>
                  <StatusBadge status={incident.status || 'Unknown'} tone={statusTone(incident.status)} />
                </div>
                <p className="incident-card-description">{incident.description}</p>
                <p className="incident-card-location">{incident.location}</p>
                <button
                  className="text-button"
                  type="button"
                  aria-label={`View details for incident ${incident.id}`}
                  aria-expanded={selectedId === incident.id}
                  onClick={() => selectIncident(incident.id)}
                >
                  View details
                </button>
              </article>
            ))}
          </div>
        )}
      </section>

      {selectedId !== null && (
        <section className="incident-detail-panel" aria-labelledby="incident-detail-title">
          <div className="incident-section-heading">
            <div>
              <h2 id="incident-detail-title">Incident details</h2>
              <p>Retrieved from the incident detail endpoint.</p>
            </div>
            <button className="secondary-button" type="button" onClick={() => setSelectedId(null)}>
              Close details
            </button>
          </div>
          {detailLoading && <LoadingState label={`Loading incident ${selectedId}`} />}
          {detailError && (
            <ErrorState
              title="Unable to load incident details"
              message={detailError.message}
              onRetry={retryDetail}
            />
          )}
          {detail && (
            <dl className="incident-detail-grid">
              <div><dt>Reference</dt><dd>#{detail.id}</dd></div>
              <div><dt>Incident type</dt><dd>{detail.incidentType}</dd></div>
              <div><dt>Status</dt><dd><StatusBadge status={detail.status || 'Unknown'} tone={statusTone(detail.status)} /></dd></div>
              <div><dt>Location</dt><dd>{detail.location}</dd></div>
              {audience !== 'citizen' && <div><dt>Contact number</dt><dd>{detail.contactNumber || 'Not provided'}</dd></div>}
              {audience !== 'citizen' && <div><dt>Assigned team</dt><dd>{detail.assignedTeam || 'Not assigned'}</dd></div>}
              <div className="incident-detail-wide"><dt>Description</dt><dd>{detail.description}</dd></div>
              {audience === 'admin' && <div><dt>Severity</dt><dd>{detail.severity || 'Not provided'}</dd></div>}
              {audience === 'admin' && <div><dt>Affected people</dt><dd>{detail.affectedPeople ?? 'Not provided'}</dd></div>}
              {audience !== 'citizen' && <div className="incident-detail-wide"><dt>Required resources</dt><dd>{detail.requiredResources || 'Not provided'}</dd></div>}
            </dl>
          )}
          {audience === 'rescue' && detail && nextStatus[detail.status] && (
            <div className="form-actions">
              <button className="primary-button" type="button" onClick={advanceStatus} disabled={statusUpdating}>
                {statusUpdating ? 'Updating status…' : `Advance to ${nextStatus[detail.status]}`}
              </button>
              {statusError && <p role="alert">{statusError.message}</p>}
            </div>
          )}
        </section>
      )}
    </div>
  )
}

export default IncidentManagement
