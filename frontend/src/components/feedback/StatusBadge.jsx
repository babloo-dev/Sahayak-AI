import './feedback.css'

const allowedTones = new Set(['neutral', 'info', 'success', 'warning', 'danger'])

function StatusBadge({ status = 'Unknown', tone = 'neutral', className = '' }) {
  const safeTone = allowedTones.has(tone) ? tone : 'neutral'
  const classes = [
    'status-badge',
    `status-badge-${safeTone}`,
    className,
  ]
    .filter(Boolean)
    .join(' ')

  return (
    <span className={classes}>
      <span className="status-badge-dot" aria-hidden="true" />
      <span>{status}</span>
    </span>
  )
}

export default StatusBadge
