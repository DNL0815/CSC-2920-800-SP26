import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import HomePage from './homePage';

describe('HomePage', () => {
  const mockPoliticians = [
    {
      bioguideId: 'P000001',
      name: 'John Doe',
      partyName: 'Democratic',
      chamber: 'House',
      district: '1',
      imageUrl: 'http://example.com/john.jpg',
      state: 'CA',
      startYear: 2021
    }
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows loading state initially', () => {
    vi.mocked(fetch).mockImplementationOnce(() => new Promise(() => {}));
    
    render(<HomePage />);
    expect(screen.getByText('Loading Congress Data...')).toBeInTheDocument();
  });

  it('displays politicians after successful fetch', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => mockPoliticians
    } as Response);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Democratic')).toBeInTheDocument();
      expect(screen.getByText('House')).toBeInTheDocument();
      expect(screen.getByText('CA')).toBeInTheDocument();
    });
  });

  it('handles fetch error gracefully', async () => {
    vi.mocked(fetch).mockRejectedValueOnce(new Error('Network error'));

    render(<HomePage />);

    await waitFor(() => {
      // Should still show loading or error state
      expect(screen.getByText('Loading Congress Data...')).toBeInTheDocument();
    });
  });

  it('shows no data message when response is empty', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => []
    } as Response);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('No data found in the database.')).toBeInTheDocument();
    });
  });

  it('displays correct member count', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => mockPoliticians
    } as Response);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Viewing 1 imported members')).toBeInTheDocument();
    });
  });
});