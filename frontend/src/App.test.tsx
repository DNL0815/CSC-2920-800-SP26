import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import App from './App';

// Mock the components to avoid actual API calls
vi.mock('./components/navbar/navbar', () => ({
  default: () => <nav data-testid="mock-navbar">Navbar</nav>
}));

vi.mock('./components/homePage/homePage', () => ({
  default: () => <div data-testid="mock-homepage">Home Page</div>
}));

vi.mock('./components/searchBar/searchBar', () => ({
  default: () => <div data-testid="mock-searchbar">Search Bar</div>
}));

vi.mock('./components/displayPage/displayPage', () => ({
  default: () => <div data-testid="mock-displaypage">Display Page</div>
}));

describe('App Component', () => {
  it('renders navbar and searchbar', () => {
    render(
      <MemoryRouter>
        <App />
      </MemoryRouter>
    );
    
    expect(screen.getByTestId('mock-navbar')).toBeInTheDocument();
    expect(screen.getByTestId('mock-searchbar')).toBeInTheDocument();
  });

  it('renders home page by default', () => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>
    );
    
    expect(screen.getByTestId('mock-homepage')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-displaypage')).not.toBeInTheDocument();
  });

  it('renders display page on /display route', () => {
    render(
      <MemoryRouter initialEntries={['/display']}>
        <App />
      </MemoryRouter>
    );
    
    expect(screen.getByTestId('mock-displaypage')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-homepage')).not.toBeInTheDocument();
  });
});