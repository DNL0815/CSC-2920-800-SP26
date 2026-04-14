import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import DisplayPage from './displayPage';

describe('DisplayPage', () => {
  const mockPoliticians = [
    {
      bioguideId: 'P000001',
      name: 'John Doe',
      partyName: 'Democratic',
      chamber: 'House of Representatives',
      district: '1',
      imageUrl: 'http://example.com/image.jpg',
      state: 'California',
      startYear: 2021
    },
    {
      bioguideId: 'P000002',
      name: 'Jane Smith',
      partyName: 'Republican',
      chamber: 'Senate',
      district: '',
      imageUrl: '',
      state: 'Texas',
      startYear: 2019
    }
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    // Use vi.stubGlobal instead of directly assigning to global.fetch
    vi.stubGlobal('fetch', vi.fn());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  it('shows loading state initially', () => {
    vi.mocked(fetch).mockImplementationOnce(() => new Promise(() => {}));
    
    render(
      <MemoryRouter initialEntries={['/display?q=test']}>
        <DisplayPage />
      </MemoryRouter>
    );
    
    expect(screen.getByText('Loading Search Results...')).toBeInTheDocument();
  });

  it('displays politicians after successful fetch', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => mockPoliticians
    } as Response);

    render(
      <MemoryRouter initialEntries={['/display']}>
        <DisplayPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    });
  });

  it('displays error message when fetch fails', async () => {
    vi.mocked(fetch).mockRejectedValueOnce(new Error('Network error'));

    render(
      <MemoryRouter initialEntries={['/display']}>
        <DisplayPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Error')).toBeInTheDocument();
      expect(screen.getByText('Failed to load members. Please try again later.')).toBeInTheDocument();
    });
  });

  it('shows no results message when filter returns empty array', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => []
    } as Response);

    render(
      <MemoryRouter initialEntries={['/display?q=nonexistent']}>
        <DisplayPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('No members found')).toBeInTheDocument();
    });
  });

  it('filters by search term', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => mockPoliticians
    } as Response);

    render(
      <MemoryRouter initialEntries={['/display?q=john']}>
        <DisplayPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.queryByText('Jane Smith')).not.toBeInTheDocument();
    });
  });

  it('filters by chamber', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => mockPoliticians
    } as Response);

    render(
      <MemoryRouter initialEntries={['/display?chamber=Senate']}>
        <DisplayPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
      expect(screen.queryByText('John Doe')).not.toBeInTheDocument();
    });
  });

  it('filters by party', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => mockPoliticians
    } as Response);

    render(
      <MemoryRouter initialEntries={['/display?party=Democratic']}>
        <DisplayPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.queryByText('Jane Smith')).not.toBeInTheDocument();
    });
  });

  it('handles image loading error', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => [mockPoliticians[1]]
    } as Response);

    render(
      <MemoryRouter initialEntries={['/display']}>
        <DisplayPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      const img = screen.getByAltText('Jane Smith') as HTMLImageElement;
      expect(img.src).toContain('placeholder');
    });
  });

  it('displays correct result count', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => mockPoliticians
    } as Response);

    render(
      <MemoryRouter initialEntries={['/display']}>
        <DisplayPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/2 members found/i)).toBeInTheDocument();
    });
  });
});