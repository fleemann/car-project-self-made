import { useState } from 'react'

const formatPrice = (price) =>
  new Intl.NumberFormat('de-DE', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)

export default function RentModal({ car, startDate: initialStartDate, endDate: initialEndDate, onConfirm, onClose }) {
  const [startDate, setStartDate] = useState(initialStartDate || '')
  const [endDate, setEndDate] = useState(initialEndDate || '')
  const [error, setError] = useState('')

  const handleSubmit = (event) => {
    event.preventDefault()

    if (!startDate || !endDate) {
      setError('Bitte Start- und Enddatum auswählen.')
      return
    }

    if (endDate < startDate) {
      setError('Enddatum muss nach dem Startdatum liegen.')
      return
    }

    setError('')

    onConfirm({
      carId: car.id,
      startDate,
      endDate
    })
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" role="dialog" aria-modal="true" onClick={(event) => event.stopPropagation()}>
        <h2>
          {car.brand} {car.model} mieten
        </h2>

        <p className="muted">
          {formatPrice(car.price)} / Tag
        </p>

        <form onSubmit={handleSubmit}>
          <div className="field">
            <label>Startdatum</label>
            <input
              type="date"
              required
              value={startDate}
              onChange={(event) => setStartDate(event.target.value)}
            />
          </div>

          <div className="field field-spaced">
            <label>Enddatum</label>
            <input
              type="date"
              required
              min={startDate}
              value={endDate}
              onChange={(event) => setEndDate(event.target.value)}
            />
          </div>

          {error && (
            <p className="message message--error">
              {error}
            </p>
          )}

          <div className="modal-actions">
            <button type="submit">
              Jetzt mieten
            </button>

            <button type="button" className="secondary" onClick={onClose}>
              Abbrechen
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}