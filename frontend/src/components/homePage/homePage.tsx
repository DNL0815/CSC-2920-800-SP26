import "./homePage.css";
import { useEffect, useState } from "react";

// 1. Types match the JSON keys sent by your Java MemberController
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

const HomePage = () => {
  const [politicians, setPoliticians] = useState<Politician[]>([]);
  const [loading, setLoading] = useState(true);
  
  // Update this to point to an actual image in your public folder 
  // or a placeholder URL for testing
  const fallbackImage = "https://via.placeholder.com/150x200?text=No+Photo";

  useEffect(() => {
    // 2. Fetching from the /db endpoint we just verified with curl
    fetch("http://localhost:8080/api/members/db")
      .then((res) => {
        if (!res.ok) throw new Error("Backend not responding");
        return res.json();
      })
      .then((data) => {
        setPoliticians(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Failed to load politicians:", err);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <div className="loading-state">
        <h2>Loading Congress Data...</h2>
        <p>This may take a second while the database responds.</p>
      </div>
    );
  }

  return (
    <div className="container">
      <header className="page-header">
        <h1>Congress Database</h1>
        <div className="stats-bar">
          Viewing <strong>{politicians.length}</strong> imported members
        </div>
      </header>

      <div className="member-grid">
        {politicians.length === 0 ? (
          <div className="no-data">
            <p>No data found in the database.</p>
            <p>Try running the sync command in your terminal:</p>
            <code>curl -X POST http://localhost:8080/api/members/sync</code>









          </div>
        ) : (
          politicians.map((politician) => (
            <div className="member-card" key={politician.bioguideId}>
              <div className="image-container">
                <img
                  src={politician.imageUrl || fallbackImage}
                  alt={politician.name}
                  onError={(e) => {
                    (e.target as HTMLImageElement).src = fallbackImage;
                  }}
                />
              </div>

              <div className="member-info">
                <h3>{politician.name}</h3>
                <div className="badges">
                  <span className={`badge party-${politician.partyName?.toLowerCase().replace(/\s+/g, '-')}`}>
                    {politician.partyName}
                  </span>
                  <span className="badge chamber-tag">{politician.chamber}</span>
                </div>
                
                <div className="details">
                  <p><strong>State:</strong> {politician.state}</p>
                  <p><strong>District:</strong> {politician.district || "At-Large"}</p>
                  <p><strong>Term Start:</strong> {politician.startYear}</p>
                </div>

                <div className="footer-id">
                  {politician.bioguideId}
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default HomePage;
