import { useId } from 'react'
import './feedback.css'

function EmptyState({
  title = 'Nothing to show yet',
  description = 'There is no information available here yet.',
  actionLabel,
  onAction,
  className = '',
}) {
  const titleId = useId()
  const classes = ['feedback-state', 'empty-state', className]
    .filter(Boolean)
    .join(' ')

  return (
    <section className={classes} aria-labelledby={titleId}>
      <span className="empty-state-mark" aria-hidden="true">—</span>
      <div className="feedback-copy">
        <h2 className="feedback-title" id={titleId}>{title}</h2>
        <p className="feedback-description">{description}</p>
        {actionLabel && onAction && (
          <button className="feedback-action" type="button" onClick={onAction}>
            {actionLabel}
          </button>
        )}
      </div>
    </section>
  )
}

export default EmptyState
