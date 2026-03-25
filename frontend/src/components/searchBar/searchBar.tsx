import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './searchBar.css';

interface SearchBarProps {
  onSearch: (searchTerm: string, filters: SearchFilters) => void;
}

interface SearchFilters {
  chamber: string;
  party: string;
  state: string;
  year: string;
}

const SearchBar = ({ onSearch }: SearchBarProps) => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState<SearchFilters>({
    chamber: '',
    party: '',
    state: '',
    year: ''
  });

  const chambers = ['All', 'House', 'Senate'];
  const parties = ['All', 'Democratic', 'Republican', 'Independent'];
  const states = [
    'All', 'AL', 'AK', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA',
    'HI', 'ID', 'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MD', 'MA',
    'MI', 'MN', 'MS', 'MO', 'MT', 'NE', 'NV', 'NH', 'NJ', 'NM', 'NY',
    'NC', 'ND', 'OH', 'OK', 'OR', 'PA', 'RI', 'SC', 'SD', 'TN', 'TX',
    'UT', 'VT', 'VA', 'WA', 'WV', 'WI', 'WY'
  ];

  const handleSearch = () => {
    // Calliong the orginal prop function
    onSearch(searchTerm, filters);

    // Buid the URL paramters
    const params = new URLSearchParams();
    if (searchTerm) params.append('q', searchTerm);
    if (filters.chamber) params.append('chamber', filters.chamber);
    if (filters.party) params.append('party', filters.party);
    if (filters.state) params.append('state', filters.state);
    if (filters.year) params.append('year', filters.year);

    // Navigate to the display page
    navigate(`/display?${params.toString()}`);
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const clearFilters = () => {
    setFilters({
      chamber: '',
      party: '',
      state: '',
      year: ''
    });
    setSearchTerm('');
    onSearch('', {
      chamber: '',
      party: '',
      state: '',
      year: ''
    });
  };

  return (
    <div className="search-bar-container">
      <div className="search-input-group">
        <input
          type="text"
          className="search-input"
          placeholder="Search by name..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyPress={handleKeyPress}
        />
        <button className="search-button" onClick={handleSearch}>
          <i className="pi pi-search"></i> Search
        </button>
        <button 
          className={`filter-toggle ${showFilters ? 'active' : ''}`}
          onClick={() => setShowFilters(!showFilters)}
        >
          <i className="pi pi-filter"></i>
          Filters
        </button>
        {(searchTerm || filters.chamber || filters.party || filters.state || filters.year) && (
          <button className="clear-button" onClick={clearFilters}>
            <i className="pi pi-times"></i>
            Clear
          </button>
        )}
      </div>

      {showFilters && (
        <div className="filters-panel">
          <div className="filter-row">
            <div className="filter-group">
              <label>Chamber:</label>
              <select 
                value={filters.chamber} 
                onChange={(e) => setFilters({...filters, chamber: e.target.value})}
              >
                {chambers.map(chamber => (
                  <option key={chamber} value={chamber === 'All' ? '' : chamber}>
                    {chamber}
                  </option>
                ))}
              </select>
            </div>

            <div className="filter-group">
              <label>Party:</label>
              <select 
                value={filters.party} 
                onChange={(e) => setFilters({...filters, party: e.target.value})}
              >
                {parties.map(party => (
                  <option key={party} value={party === 'All' ? '' : party}>
                    {party}
                  </option>
                ))}
              </select>
            </div>

            <div className="filter-group">
              <label>State:</label>
              <select 
                value={filters.state} 
                onChange={(e) => setFilters({...filters, state: e.target.value})}
              >
                {states.map(state => (
                  <option key={state} value={state === 'All' ? '' : state}>
                    {state}
                  </option>
                ))}
              </select>
            </div>

            <div className="filter-group">
              <label>Start Year:</label>
              <input
                type="number"
                placeholder="e.g., 2023"
                value={filters.year}
                onChange={(e) => setFilters({...filters, year: e.target.value})}
                min="1900"
                max="2026"
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchBar;