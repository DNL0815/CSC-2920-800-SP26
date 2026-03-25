// DisplayPage.tsx
import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import './displayPage.css';

type Politician = {
  bioguideId: string;
  name: string;
  partyName: string;
  chamber: string;
  district: string;
  imageUrl: string;
  state: string;
  startYear: number;
};

const DisplayPage = () => {
  const [searchParams] = useSearchParams();
  const [politicians, setPoliticians] = useState<Politician[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  const fallbackImage = "https://via.placeholder.com/150x200?text=No+Photo";

  // Get search parameters from URL
  const searchTerm = searchParams.get('q') || '';
  const chamberFilter = searchParams.get('chamber') || '';
  const partyFilter = searchParams.get('party') || '';
  const stateFilter = searchParams.get('state') || '';
  const yearFilter = searchParams.get('year') || '';

  useEffect(() => {
    const fetchFilteredMembers = async () => {
      setLoading(true);
      setError(null);
      
      try {
        // First fetch all members
        const response = await fetch("http://localhost:8080/api/members/db");
        
        if (!response.ok) {
          throw new Error("Failed to fetch members");
        }
        
        const allMembers: Politician[] = await response.json();
        
        // Apply filters client-side
        let filteredMembers = [...allMembers];
        
        // Filter by search term (name)
        if (searchTerm) {
          filteredMembers = filteredMembers.filter(member =>
            member.name.toLowerCase().includes(searchTerm.toLowerCase())
          );
        }
        
        // Filter by chamber
        if (chamberFilter) {
          filteredMembers = filteredMembers.filter(member =>
            member.chamber.toLowerCase() === chamberFilter.toLowerCase()
          );
        }
        
        // Filter by party
        if (partyFilter) {
          filteredMembers = filteredMembers.filter(member =>
            member.partyName.toLowerCase() === partyFilter.toLowerCase()
          );
        }
        
        // Filter by state
        if (stateFilter) {
          filteredMembers = filteredMembers.filter(member =>
            member.state === stateFilter
          );
        }
        
        // Filter by start year
        if (yearFilter) {
          filteredMembers = filteredMembers.filter(member =>
            member.startYear === parseInt(yearFilter)
          );
        }
        
        setPoliticians(filteredMembers);
      } catch (err) {
        console.error("Error fetching members:", err);
        setError("Failed to load members. Please try again later.");
      } finally {
        setLoading(false);
      }
    };
    
    fetchFilteredMembers();
  }, [searchTerm, chamberFilter, partyFilter, stateFilter, yearFilter]);

  // Helper function to build the search summary text
  const getSearchSummary = () => {
    const filters = [];
    if (searchTerm) filters.push(`name contains "${searchTerm}"`);
    if (chamberFilter) filters.push(`${chamberFilter} chamber`);
    if (partyFilter) filters.push(`${partyFilter} party`);
    if (stateFilter) filters.push(`state: ${stateFilter}`);
    if (yearFilter) filters.push(`start year: ${yearFilter}`);
    
    if (filters.length === 0) return "All members";
    return filters.join(", ");
  };

  if (loading) {
    return (
      <div className="display-loading">
        <div className="loading-spinner"></div>
        <h2>Loading Search Results...</h2>
        <p>Finding members matching: {getSearchSummary()}</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="display-error">
        <h2>Error</h2>
        <p>{error}</p>
        <button onClick={() => window.location.reload()}>Try Again</button>
      </div>
    );
  }

  return (
    <div className="display-container">
      <header className="display-header">
        <h1>Search Results</h1>
        <div className="search-summary">
          <div className="filters-applied">
            <strong>Showing:</strong> {getSearchSummary()}
          </div>
          <div className="result-count">
            <strong>{politicians.length}</strong> member{politicians.length !== 1 ? 's' : ''} found
          </div>
        </div>
      </header>

      {politicians.length === 0 ? (
        <div className="no-results">
          <i className="pi pi-search" style={{ fontSize: '48px', color: '#999' }}></i>
          <h3>No members found</h3>
          <p>Try adjusting your search criteria or clearing some filters.</p>
          <div className="suggestions">
            <p>Suggestions:</p>
            <ul>
              <li>Check for typos in the name</li>
              <li>Try a different chamber (House or Senate)</li>
              <li>Expand your year range</li>
              <li>Remove some filters to see more results</li>
            </ul>
          </div>
        </div>
      ) : (
        <div className="display-grid">
          {politicians.map((politician) => (
            <div className="display-card" key={politician.bioguideId}>
              <div className="card-image">
                <img
                  src={politician.imageUrl || fallbackImage}
                  alt={politician.name}
                  onError={(e) => {
                    (e.target as HTMLImageElement).src = fallbackImage;
                  }}
                />
              </div>
              
              <div className="card-content">
                <h3>{politician.name}</h3>
                
                <div className="card-badges">
                  <span className={`badge party-${politician.partyName?.toLowerCase().replace(/\s+/g, '-')}`}>
                    {politician.partyName}
                  </span>
                  <span className="badge chamber-badge">{politician.chamber}</span>
                </div>
                
                <div className="card-details">
                  <div className="detail-row">
                    <span className="detail-label">State:</span>
                    <span className="detail-value">{politician.state}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">District:</span>
                    <span className="detail-value">{politician.district || "At-Large"}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Term Start:</span>
                    <span className="detail-value">{politician.startYear}</span>
                  </div>
                </div>
                
                <div className="card-footer">
                  <span className="bioguide-id">ID: {politician.bioguideId}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DisplayPage;