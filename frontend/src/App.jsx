import { useEffect, useState } from 'react'
import { carApi } from './api/carApi'
import SearchFilters from './components/SearchFilters'
import CarGrid from './components/CarGrid'
import AdminPanel from './components/AdminPanel'
import RentModal from './components/RentModal'

const defaultFilters = {
  brand: '',
  model: '',
  gear: '',
  engine: '',
  minPrice: '',
  maxPrice: '',
  psMin: '',
  psMax: '',
  yearMin: '',
  seats: '',
  startDate: '',
  endDate: '',
  sort: 'priceAsc'
}

export default function App() {
  const [cars, setCars] = useState([])
  const [filters, setFilters] = useState(defaultFilters)
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState({ text: '', error: false })
  const [showAdmin, setShowAdmin] = useState(false)
  const [editingCar, setEditingCar] = useState(null)
  const [rentingCar, setRentingCar] = useState(null)

  async function loadCars(params = filters) {
    setLoading(true)
    setMessage({ text: '', error: false })

    try {
      const data = await carApi.search(params)
      setCars(data)
    } catch (error) {
      setMessage({ text: error.message, error: true })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCars(defaultFilters)
  }, [])

  function handleSearch() {
    loadCars(filters)
  }

  function handleReset() {
    setFilters(defaultFilters)
    setEditingCar(null)
    setRentingCar(null)
    loadCars(defaultFilters)
  }

  async function handleSaveCar(carData, id) {
    try {
      if (id) {
        await carApi.update(id, carData)
        setMessage({ text: 'Auto aktualisiert.', error: false })
      } else {
        await carApi.create(carData)
        setMessage({ text: 'Auto erstellt.', error: false })
      }

      setEditingCar(null)
      await loadCars()
    } catch (error) {
      setMessage({ text: error.message, error: true })
    }
  }

  async function handleDeleteCar(id) {
    const confirmed = confirm('Auto wirklich löschen?')

    if (!confirmed) {
      return
    }

    try {
      await carApi.delete(id)
      setMessage({ text: 'Auto gelöscht.', error: false })
      await loadCars()
    } catch (error) {
      setMessage({ text: error.message, error: true })
    }
  }

  async function handleRent({ carId, startDate, endDate }) {
    try {
      await carApi.rent(carId, { startDate, endDate })

      setMessage({
        text: `${rentingCar.brand} ${rentingCar.model} wurde gemietet.`,
        error: false
      })

      setRentingCar(null)
      await loadCars()
    } catch (error) {
      setMessage({ text: error.message, error: true })
    }
  }

  function handleEditCar(car) {
    setEditingCar(car)
    setShowAdmin(true)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  function handleCloseAdmin() {
    setShowAdmin((currentValue) => !currentValue)
    setEditingCar(null)
  }

  return (
    <>
      <header className="app-header">
        <div>
          <p className="eyebrow">Autovermietung</p>
          <h1>Autos suchen, verwalten und mieten</h1>
        </div>

        <div className="header-actions">
          <button className="secondary" onClick={handleReset}>
            Zurücksetzen
          </button>

          <button onClick={handleCloseAdmin}>
            {showAdmin ? 'Admin schliessen' : 'Admin'}
          </button>
        </div>
      </header>

      <main>
        {showAdmin && (
          <AdminPanel
            editingCar={editingCar}
            onSave={handleSaveCar}
            onReset={() => setEditingCar(null)}
          />
        )}

        <SearchFilters
          filters={filters}
          onChange={setFilters}
          onSearch={handleSearch}
        />

        {message.text && (
          <p className={`message ${message.error ? 'message--error' : 'message--success'}`}>
            {message.text}
          </p>
        )}

        <CarGrid
          cars={cars}
          loading={loading}
          onEdit={handleEditCar}
          onDelete={handleDeleteCar}
          onRent={setRentingCar}
        />
      </main>

      {rentingCar && (
        <RentModal
          car={rentingCar}
          startDate={filters.startDate}
          endDate={filters.endDate}
          onConfirm={handleRent}
          onClose={() => setRentingCar(null)}
        />
      )}
    </>
  )
}