import './feedback.css'

function StaleDataIndicator({
  label = 'This information may be out of date',
  lastUpdated,
  className = '',
}) {
  const classes = ['stale-data-indicator', className].filter(Boolean).join(' ')

  return (
    <div className={classes} role="status">
      <span className="state-symbol stale-symbol" aria-hidden="true">!</span>
      <span className="stale-data-copy">
        <span className="stale-data-label">{label}</span>
        {lastUpdated && (
          <span className="stale-data-updated">Last updated: {lastUpdated}</span>
        )}
      </span>
    </div>
  )
}

export default StaleDataIndicator
