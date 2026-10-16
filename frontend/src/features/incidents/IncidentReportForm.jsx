import { useState } from 'react'
import { createIncident } from '../../services/incidentsApi.js'
import { ErrorState, LoadingState } from '../../components/feedback/index.js'
import './incident-pages.css'

const incidentTypes = [
  'Flood',
  'Landslide',
  'Earthquake',
  'Cyclone',
  'Severe storm / thunderstorm / lightning / strong wind',
  'Heatwave',
  'Drought',
  'Other',
]

function IncidentReportForm() {
  const [form, setForm] = useState({
    incidentType: '',
    description: '',
    location: '',
    contactNumber: '',
  })
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)
  const [receipt, setReceipt] = useState(null)

  function updateField(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
    setError(null)
    setReceipt(null)
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSubmitting(true)
    setError(null)
    setReceipt(null)
    try {
      const result = await createIncident(form)
      setReceipt(result)
      setForm({ incidentType: '', description: '', location: '', contactNumber: '' })
    } catch (requestError) {
      setError(requestError)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="incident-workspace">
      <p className="integration-note" role="note">
        This report is linked to your authenticated local development account. This token-based
        development login is not production OTP authentication or an emergency service.
      </p>

      {receipt && (
        <section className="submission-receipt" role="status" aria-live="polite">
          <div>
            <h2>Report submitted</h2>
            <p>Reference #{receipt.id} · Status: <strong>{receipt.status}</strong></p>
          </div>
        </section>
      )}

      {error && <ErrorState title="Report was not submitted" message={error.message} />}

      <form className="incident-form" onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="incident-type">Incident type</label>
          <select
            id="incident-type"
            name="incidentType"
            value={form.incidentType}
            onChange={updateField}
            required
            disabled={submitting}
          >
            <option value="">Select an incident type</option>
            {incidentTypes.map((type) => <option key={type} value={type}>{type}</option>)}
          </select>
        </div>
        <div className="form-field">
          <label htmlFor="incident-description">Description</label>
          <textarea
            id="incident-description"
            name="description"
            value={form.description}
            onChange={updateField}
            maxLength={255}
            rows={4}
            required
            disabled={submitting}
          />
          <span className="field-hint">Up to 255 characters.</span>
        </div>
        <div className="form-field">
          <label htmlFor="incident-location">Location</label>
          <input
            id="incident-location"
            name="location"
            value={form.location}
            onChange={updateField}
            maxLength={255}
            autoComplete="street-address"
            required
            disabled={submitting}
          />
        </div>
        <div className="form-field">
          <label htmlFor="incident-contact">Contact number</label>
          <input
            id="incident-contact"
            name="contactNumber"
            type="tel"
            value={form.contactNumber}
            onChange={updateField}
            maxLength={255}
            autoComplete="tel"
            required
            disabled={submitting}
          />
        </div>
        <div className="form-actions">
          <button className="primary-button" type="submit" disabled={submitting}>
            {submitting ? 'Submitting…' : 'Submit incident report'}
          </button>
          {submitting && <LoadingState label="Submitting report" inline />}
        </div>
      </form>
    </div>
  )
}

export default IncidentReportForm
