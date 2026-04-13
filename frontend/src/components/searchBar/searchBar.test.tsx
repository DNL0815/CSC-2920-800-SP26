import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import SearchBar from './searchBar';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate
  };
});

describe('SearchBar', () => {
  const mockOnSearch = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderSearchBar = () => {
    return render(
      <BrowserRouter>
        <SearchBar onSearch={mockOnSearch} />
      </BrowserRouter>
    );
  };

  it('renders search input and buttons', () => {
    renderSearchBar();
    
    expect(screen.getByPlaceholderText('Search by name...')).toBeInTheDocument();
    expect(screen.getByText('Search')).toBeInTheDocument();
    expect(screen.getByText('Filters')).toBeInTheDocument();
  });

  it('updates search term on input change', () => {
    renderSearchBar();
    const input = screen.getByPlaceholderText('Search by name...');
    
    fireEvent.change(input, { target: { value: 'John Doe' } });
    expect(input).toHaveValue('John Doe');
  });

  it('calls onSearch and navigates when search button is clicked', () => {
    renderSearchBar();
    const input = screen.getByPlaceholderText('Search by name...');
    const searchButton = screen.getByText('Search');
    
    fireEvent.change(input, { target: { value: 'John' } });
    fireEvent.click(searchButton);
    
    expect(mockOnSearch).toHaveBeenCalledWith('John', {
      chamber: '',
      party: '',
      state: '',
      year: ''
    });
    expect(mockNavigate).toHaveBeenCalledWith('/display?q=John');
  });

  it('calls onSearch when Enter key is pressed', () => {
    renderSearchBar();
    const input = screen.getByPlaceholderText('Search by name...');
    
    fireEvent.change(input, { target: { value: 'Jane' } });
    fireEvent.keyPress(input, { key: 'Enter', code: 'Enter', charCode: 13 });
    
    expect(mockOnSearch).toHaveBeenCalledWith('Jane', expect.any(Object));
  });

  it('toggles filters panel when filter button is clicked', () => {
    renderSearchBar();
    const filterButton = screen.getByText('Filters');
    
    // Initially filters should not be visible
    expect(screen.queryByText('Chamber:')).not.toBeInTheDocument();
    
    // Click to show filters
    fireEvent.click(filterButton);
    expect(screen.getByText('Chamber:')).toBeInTheDocument();
    
    // Click to hide filters
    fireEvent.click(filterButton);
    expect(screen.queryByText('Chamber:')).not.toBeInTheDocument();
  });

  it('applies chamber filter', () => {
    renderSearchBar();
    const filterButton = screen.getByText('Filters');
    fireEvent.click(filterButton);
    
    // Use getByRole instead of getByLabelText
    const chamberSelect = screen.getByRole('combobox', { name: /chamber/i });
    fireEvent.change(chamberSelect, { target: { value: 'Senate' } });
    
    expect(chamberSelect).toHaveValue('Senate');
  });

  it('applies party filter', () => {
    renderSearchBar();
    const filterButton = screen.getByText('Filters');
    fireEvent.click(filterButton);
    
    // Use getByRole instead of getByLabelText
    const partySelect = screen.getByRole('combobox', { name: /party/i });
    fireEvent.change(partySelect, { target: { value: 'Democratic' } });
    
    expect(partySelect).toHaveValue('Democratic');
  });

  it('applies year filter', () => {
    renderSearchBar();
    const filterButton = screen.getByText('Filters');
    fireEvent.click(filterButton);
    
    // Use getByPlaceholderText for the year input
    const yearInput = screen.getByPlaceholderText('e.g., 2023');
    fireEvent.change(yearInput, { target: { value: '2021' } });
    
    expect(yearInput).toHaveValue(2021);
  });

  it('clears all filters when clear button is clicked', () => {
    renderSearchBar();
    
    // Set some values
    const input = screen.getByPlaceholderText('Search by name...');
    fireEvent.change(input, { target: { value: 'Test' } });
    
    const filterButton = screen.getByText('Filters');
    fireEvent.click(filterButton);
    
    // Use getByRole for selects
    const chamberSelect = screen.getByRole('combobox', { name: /chamber/i });
    fireEvent.change(chamberSelect, { target: { value: 'Senate' } });
    
    const partySelect = screen.getByRole('combobox', { name: /party/i });
    fireEvent.change(partySelect, { target: { value: 'Democratic' } });
    
    // Click clear button
    const clearButton = screen.getByText('Clear');
    fireEvent.click(clearButton);
    
    // Verify everything is cleared
    expect(input).toHaveValue('');
    expect(mockOnSearch).toHaveBeenCalledWith('', {
      chamber: '',
      party: '',
      state: '',
      year: ''
    });
  });

  it('clear button only appears when filters or search term exist', () => {
    renderSearchBar();
    
    // Initially no clear button
    expect(screen.queryByText('Clear')).not.toBeInTheDocument();
    
    // Add search term
    const input = screen.getByPlaceholderText('Search by name...');
    fireEvent.change(input, { target: { value: 'Test' } });
    
    // Clear button appears
    expect(screen.getByText('Clear')).toBeInTheDocument();
  });

  it('navigates with all filters applied', () => {
    renderSearchBar();
    
    // Set search term
    const input = screen.getByPlaceholderText('Search by name...');
    fireEvent.change(input, { target: { value: 'John' } });
    
    // Open and set filters
    const filterButton = screen.getByText('Filters');
    fireEvent.click(filterButton);
    
    // Use getByRole for selects
    const chamberSelect = screen.getByRole('combobox', { name: /chamber/i });
    fireEvent.change(chamberSelect, { target: { value: 'Senate' } });
    
    const partySelect = screen.getByRole('combobox', { name: /party/i });
    fireEvent.change(partySelect, { target: { value: 'Democratic' } });
    
    const stateSelect = screen.getByRole('combobox', { name: /state/i });
    fireEvent.change(stateSelect, { target: { value: 'CA' } });
    
    const yearInput = screen.getByPlaceholderText('e.g., 2023');
    fireEvent.change(yearInput, { target: { value: '2021' } });
    
    // Perform search
    const searchButton = screen.getByText('Search');
    fireEvent.click(searchButton);
    
    // Verify navigation URL contains all params
    expect(mockNavigate).toHaveBeenCalledWith(
      expect.stringContaining('/display?q=John&chamber=Senate&party=Democratic&state=California&year=2021')
    );
  });
});