const BASE = '/api/v1/car'

async function request(url, options = {}) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  })

  if (res.status === 204) {
    return null
  }

  const data = await res.json()

  if (!res.ok) {
    throw new Error(data.error || 'Anfrage fehlgeschlagen')
  }

  return data
}

export const carApi = {
  search(params) {
    const url = new URL(BASE, window.location.origin)

    Object.entries(params).forEach(([key, value]) => {
      if (value !== '' && value != null) {
        url.searchParams.set(key, value)
      }
    })

    return request(url.pathname + url.search)
  },

  getById(id) {
    return request(`${BASE}/${id}`)
  },

  create(car) {
    return request(BASE, {
      method: 'POST',
      body: JSON.stringify(car)
    })
  },

  update(id, car) {
    return request(`${BASE}/${id}`, {
      method: 'PUT',
      body: JSON.stringify(car)
    })
  },

  delete(id) {
    return request(`${BASE}/${id}`, {
      method: 'DELETE'
    })
  },

  rent(id, rental) {
    return request(`${BASE}/${id}/rentals`, {
      method: 'POST',
      body: JSON.stringify(rental)
    })
  }
}