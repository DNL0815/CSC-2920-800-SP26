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

  const chambers = ['All', 'House of Representatives', 'Senate'];
  const parties = ['All', 'Democratic', 'Republican', 'Independent'];
  const states = [
    { abbr: 'AL', name: 'Alabama' },
    { abbr: 'AK', name: 'Alaska' },
    { abbr: 'AZ', name: 'Arizona' },
    { abbr: 'AR', name: 'Arkansas' },
    { abbr: 'CA', name: 'California' },
    { abbr: 'CO', name: 'Colorado' },
    { abbr: 'CT', name: 'Connecticut' },
    { abbr: 'DE', name: 'Delaware' },
    { abbr: 'FL', name: 'Florida' },
    { abbr: 'GA', name: 'Georgia' },
    { abbr: 'HI', name: 'Hawaii' },
    { abbr: 'ID', name: 'Idaho' },
    { abbr: 'IL', name: 'Illinois' },
    { abbr: 'IN', name: 'Indiana' },
    { abbr: 'IA', name: 'Iowa' },
    { abbr: 'KS', name: 'Kansas' },
    { abbr: 'KY', name: 'Kentucky' },
    { abbr: 'LA', name: 'Louisiana' },
    { abbr: 'ME', name: 'Maine' },
    { abbr: 'MD', name: 'Maryland' },
    { abbr: 'MA', name: 'Massachusetts' },
    { abbr: 'MI', name: 'Michigan' },
    { abbr: 'MN', name: 'Minnesota' },
    { abbr: 'MS', name: 'Mississippi' },
    { abbr: 'MO', name: 'Missouri' },
    { abbr: 'MT', name: 'Montana' },
    { abbr: 'NE', name: 'Nebraska' },
    { abbr: 'NV', name: 'Nevada' },
    { abbr: 'NH', name: 'New Hampshire' },
    { abbr: 'NJ', name: 'New Jersey' },
    { abbr: 'NM', name: 'New Mexico' },
    { abbr: 'NY', name: 'New York' },
    { abbr: 'NC', name: 'North Carolina' },
    { abbr: 'ND', name: 'North Dakota' },
    { abbr: 'OH', name: 'Ohio' },
    { abbr: 'OK', name: 'Oklahoma' },
    { abbr: 'OR', name: 'Oregon' },
    { abbr: 'PA', name: 'Pennsylvania' },
    { abbr: 'RI', name: 'Rhode Island' },
    { abbr: 'SC', name: 'South Carolina' },
    { abbr: 'SD', name: 'South Dakota' },
    { abbr: 'TN', name: 'Tennessee' },
    { abbr: 'TX', name: 'Texas' },
    { abbr: 'UT', name: 'Utah' },
    { abbr: 'VT', name: 'Vermont' },
    { abbr: 'VA', name: 'Virginia' },
    { abbr: 'WA', name: 'Washington' },
    { abbr: 'WV', name: 'West Virginia' },
    { abbr: 'WI', name: 'Wisconsin' },
    { abbr: 'WY', name: 'Wyoming' }
  ];

  const handleSearch = () => {
    const selectedStateName =
    states.find(s => s.abbr === filters.state)?.name || '';

    const updatedFilters = {
      ...filters,
      state: selectedStateName
    };

    // send FULL state name
    onSearch(searchTerm, updatedFilters);

    const params = new URLSearchParams();
    if (searchTerm) params.append('q', searchTerm);
    if (filters.chamber) params.append('chamber', filters.chamber);
    if (filters.party) params.append('party', filters.party);
    if (selectedStateName) params.append('state', selectedStateName);
    if (filters.year) params.append('year', filters.year);

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
                onChange={(e) => setFilters({ ...filters, state: e.target.value })}
              >
                <option value="">All</option>

                {states.map(state => (
                  <option key={state.abbr} value={state.abbr}>
                    {state.abbr} - {state.name}
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