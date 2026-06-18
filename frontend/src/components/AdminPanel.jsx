import { useEffect, useState } from 'react'

const emptyCar = {
  brand: '',
  model: '',
  price: '',
  gear: 'automatic',
  ps: '',
  engine: 'gasoline',
  year: '',
  seats: ''
}

export default function AdminPanel({ editingCar, onSave, onReset }) {
  const [form, setForm] = useState(emptyCar)

  useEffect(() => {
    if (editingCar) {
      setForm({
        brand: editingCar.brand,
        model: editingCar.model,
        price: editingCar.price,
        gear: editingCar.gear,
        ps: editingCar.ps,
        engine: editingCar.engine,
        year: editingCar.year,
        seats: editingCar.seats
      })
    } else {
      setForm(emptyCar)
    }
  }, [editingCar])

  const set = (key) => (event) => {
    setForm((previousForm) => ({
      ...previousForm,
      [key]: event.target.value
    }))
  }

  const handleSubmit = (event) => {
    event.preventDefault()

    onSave({
      brand: form.brand.trim(),
      model: form.model.trim(),
      price: Number(form.price),
      gear: form.gear,
      ps: Number(form.ps),
      engine: form.engine,
      year: Number(form.year),
      seats: Number(form.seats)
    }, editingCar?.id)

    setForm(emptyCar)
  }

  const handleReset = () => {
    setForm(emptyCar)
    onReset()
  }

  return (
    <section className="admin-panel">
      <h2>{editingCar ? 'Auto editieren' : 'Auto hinzufügen'}</h2>

      <form className="car-form" onSubmit={handleSubmit}>
        <input
          placeholder="Marke *"
          required
          value={form.brand}
          onChange={set('brand')}
        />

        <input
          placeholder="Modell *"
          required
          value={form.model}
          onChange={set('model')}
        />

        <input
          type="number"
          step="0.01"
          min="0.01"
          placeholder="Preis / Tag *"
          required
          value={form.price}
          onChange={set('price')}
        />

        <select value={form.gear} onChange={set('gear')}>
          <option value="automatic">Automatik</option>
          <option value="manual">Manuell</option>
        </select>

        <input
          type="number"
          min="1"
          placeholder="PS *"
          required
          value={form.ps}
          onChange={set('ps')}
        />

        <select value={form.engine} onChange={set('engine')}>
          <option value="gasoline">Benzin</option>
          <option value="electro">Elektro</option>
          <option value="hybrid">Hybrid</option>
        </select>

        <input
          type="number"
          min="1901"
          placeholder="Baujahr *"
          required
          value={form.year}
          onChange={set('year')}
        />

        <input
          type="number"
          min="1"
          placeholder="Sitze *"
          required
          value={form.seats}
          onChange={set('seats')}
        />

        <button type="submit">
          {editingCar ? 'Änderungen speichern' : 'Speichern'}
        </button>

        <button type="button" className="secondary" onClick={handleReset}>
          Zurücksetzen
        </button>
      </form>
    </section>
  )
}