import { useState } from 'react'

const LABELS = {
  automatic: 'Automatik',
  manual: 'Manuell',
  electro: 'Elektro',
  gasoline: 'Benzin',
  hybrid: 'Hybrid'
}

const formatPrice = (price) =>
  new Intl.NumberFormat('de-DE', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)

export default function CarCard({ car, onEdit, onDelete, onRent }) {
  const [imageFailed, setImageFailed] = useState(false)
  const showImage = car.image && !imageFailed

  return (
    <article className="car-card">
      {showImage ? (
        <img
          className="car-image"
          src={car.image}
          alt={`${car.brand} ${car.model}`}
          loading="lazy"
          onError={() => setImageFailed(true)}
        />
      ) : (
        <div className="car-image car-image--placeholder" aria-hidden="true">
          <span>🚗</span>
          <small>{car.brand}</small>
        </div>
      )}

      <div className="car-title">
        <h3>
          {car.brand} {car.model}
        </h3>

        <span className="price">
          {formatPrice(car.price)} / Tag
        </span>
      </div>

      <dl className="specs">
        <div>
          <dt>Getriebe</dt>
          <dd>{LABELS[car.gear] ?? car.gear}</dd>
        </div>

        <div>
          <dt>Motor</dt>
          <dd>{LABELS[car.engine] ?? car.engine}</dd>
        </div>

        <div>
          <dt>PS</dt>
          <dd>{car.ps}</dd>
        </div>

        <div>
          <dt>Baujahr</dt>
          <dd>{car.year}</dd>
        </div>

        <div>
          <dt>Sitze</dt>
          <dd>{car.seats}</dd>
        </div>

        <div>
          <dt>Buchungen</dt>
          <dd>{car.rentals?.length ?? 0}</dd>
        </div>
      </dl>

      <p className="rentals">
        {car.rentals?.length > 0
          ? `Gebucht: ${car.rentals.map((rental) => `${rental.startDate}–${rental.endDate}`).join(', ')}`
          : 'Keine Buchungen'}
      </p>

      <div className="actions">
        <button onClick={onRent}>
          Mieten
        </button>

        <button className="secondary" onClick={onEdit}>
          Editieren
        </button>

        <button className="danger" onClick={onDelete}>
          Löschen
        </button>
      </div>
    </article>
  )
}