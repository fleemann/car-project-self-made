export default function SearchFilters({ filters, onChange, onSearch }) {
  const set = (key) => (event) => {
    onChange((previousFilters) => ({
      ...previousFilters,
      [key]: event.target.value
    }))
  }

  return (
    <section className="filters" aria-label="Autos suchen">
      <div className="field">
        <label>Startdatum</label>
        <input
          type="date"
          value={filters.startDate}
          onChange={set('startDate')}
        />
      </div>

      <div className="field">
        <label>Enddatum</label>
        <input
          type="date"
          value={filters.endDate}
          onChange={set('endDate')}
        />
      </div>

      <div className="field">
        <label>Marke</label>
        <input
          placeholder="z.B. BMW"
          value={filters.brand}
          onChange={set('brand')}
        />
      </div>

      <div className="field">
        <label>Modell</label>
        <input
          placeholder="z.B. Golf"
          value={filters.model}
          onChange={set('model')}
        />
      </div>

      <div className="field">
        <label>Max. Preis / Tag</label>
        <input
          type="number"
          min="0"
          placeholder="120"
          value={filters.maxPrice}
          onChange={set('maxPrice')}
        />
      </div>

      <div className="field">
        <label>Getriebe</label>
        <select value={filters.gear} onChange={set('gear')}>
          <option value="">Alle</option>
          <option value="automatic">Automatik</option>
          <option value="manual">Manuell</option>
        </select>
      </div>

      <div className="field">
        <label>Motor</label>
        <select value={filters.engine} onChange={set('engine')}>
          <option value="">Alle</option>
          <option value="electro">Elektro</option>
          <option value="gasoline">Benzin</option>
          <option value="hybrid">Hybrid</option>
        </select>
      </div>

      <div className="field">
        <label>Min. PS</label>
        <input
          type="number"
          min="0"
          placeholder="100"
          value={filters.psMin}
          onChange={set('psMin')}
        />
      </div>

      <div className="field">
        <label>Sitze mindestens</label>
        <input
          type="number"
          min="1"
          placeholder="5"
          value={filters.seats}
          onChange={set('seats')}
        />
      </div>

      <div className="field">
        <label>Baujahr ab</label>
        <input
          type="number"
          min="1900"
          placeholder="2020"
          value={filters.yearMin}
          onChange={set('yearMin')}
        />
      </div>

      <div className="field">
        <label>Sortierung</label>
        <select value={filters.sort} onChange={set('sort')}>
          <option value="priceAsc">Preis aufsteigend</option>
          <option value="priceDesc">Preis absteigend</option>
        </select>
      </div>

      <button onClick={onSearch}>Suchen</button>
    </section>
  )
}