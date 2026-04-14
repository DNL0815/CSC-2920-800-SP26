import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import App from './App';
import { vi } from 'vitest';

describe('Integration Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Use vi.stubGlobal instead of global.fetch
    vi.stubGlobal('fetch', vi.fn());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  test('complete user flow: search from home page to display page', async () => {
    const mockMembers = [
      {
        bioguideId: 'P000001',
        name: 'John Doe',
        partyName: 'Democratic',
        chamber: 'House of Representatives',
        district: '1',
        imageUrl: 'http://example.com/image.jpg',
        state: 'California',
        startYear: 2021
      }
    ];

    // Mock home page fetch
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => mockMembers
    } as Response);

    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>
    );

    // Wait for home page to load
    await waitFor(() => {
      expect(screen.getByText('Congress Database')).toBeInTheDocument();
    });

    // Type search query
    const searchInput = screen.getByPlaceholderText('Search by name...');
    await userEvent.type(searchInput, 'John');
    
    // Click search
    const searchButton = screen.getByText('Search');
    await userEvent.click(searchButton);

    // Mock display page fetch
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => mockMembers
    } as Response);

    // Verify display page shows results
    await waitFor(() => {
      expect(screen.getByText('Search Results')).toBeInTheDocument();
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
  });
});