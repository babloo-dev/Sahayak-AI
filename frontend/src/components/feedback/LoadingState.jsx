import './feedback.css'

function LoadingState({
  label = 'Loading',
  description,
  inline = false,
  className = '',
}) {
  const classes = [
    'feedback-state',
    'loading-state',
    inline ? 'loading-state-inline' : '',
    className,
  ]
    .filter(Boolean)
    .join(' ')

  return (
    <div className={classes} role="status" aria-busy="true">
      <span className="loading-spinner" aria-hidden="true" />
      <div className="feedback-copy">
        <p className="feedback-title">{label}</p>
        {description && <p className="feedback-description">{description}</p>}
      </div>
    </div>
  )
}

export default LoadingState
