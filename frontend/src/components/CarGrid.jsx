import CarCard from './CarCard'

export default function CarGrid({ cars, loading, onEdit, onDelete, onRent }) {
  return (
    <section className="panel">
      <div className="result-heading">
        <h2>Verfügbare Autos</h2>
        <span className="result-count">
          {cars.length} Auto{cars.length !== 1 ? 's' : ''}
        </span>
      </div>

      {loading ? (
        <p className="muted">Lade Autos...</p>
      ) : cars.length === 0 ? (
        <p className="muted">Keine Autos gefunden.</p>
      ) : (
        <div className="car-list">
          {cars.map((car) => (
            <CarCard
              key={car.id}
              car={car}
              onEdit={() => onEdit(car)}
              onDelete={() => onDelete(car.id)}
              onRent={() => onRent(car)}
            />
          ))}
        </div>
      )}
    </section>
  )
}