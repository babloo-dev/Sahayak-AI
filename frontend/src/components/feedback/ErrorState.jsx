import { useId } from 'react'
import './feedback.css'

function ErrorState({
  title = 'Unable to load this information',
  message = 'Please try again in a moment.',
  retryLabel = 'Try again',
  onRetry,
  className = '',
}) {
  const titleId = useId()
  const classes = ['feedback-state', 'error-state', className]
    .filter(Boolean)
    .join(' ')

  return (
    <section className={classes} role="alert" aria-labelledby={titleId}>
      <span className="state-symbol error-symbol" aria-hidden="true">!</span>
      <div className="feedback-copy">
        <h2 className="feedback-title" id={titleId}>{title}</h2>
        <p className="feedback-description">{message}</p>
        {onRetry && (
          <button className="feedback-action" type="button" onClick={onRetry}>
            {retryLabel}
          </button>
        )}
      </div>
    </section>
  )
}

export default ErrorState
