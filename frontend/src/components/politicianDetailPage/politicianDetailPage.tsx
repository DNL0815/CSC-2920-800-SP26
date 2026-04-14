import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import "./politicianDetailPage.css";

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

const PoliticianDetailPage = () => {
  const { id } = useParams();
  const [politician, setPolitician] = useState<Politician | null>(null);
  const [loading, setLoading] = useState(true);

  const fallbackImage = "https://via.placeholder.com/300x400?text=No+Photo";

  useEffect(() => {
    fetch(`http://localhost:8080/api/members/db`)
      .then((res) => res.json())
      .then((data) => {
        const found = data.find(
          (member: Politician) => member.bioguideId === id
        );
        setPolitician(found || null);
        setLoading(false);
      });
  }, [id]);

  if (loading) return <h2>Loading...</h2>;

  if (!politician) return <h2>Politician not found</h2>;

  return (
    <div className="detail-page">
      <div className="detail-card">
        <img
          src={politician.imageUrl || fallbackImage}
          alt={politician.name}
        />

        <div className="detail-info">
          <h1>{politician.name}</h1>
          <p><strong>Party:</strong> {politician.partyName}</p>
          <p><strong>Chamber:</strong> {politician.chamber}</p>
          <p><strong>State:</strong> {politician.state}</p>
          <p><strong>District:</strong> {politician.district}</p>
          <p><strong>Term Start:</strong> {politician.startYear}</p>
          <p><strong>Bioguide ID:</strong> {politician.bioguideId}</p>
        </div>
      </div>
    </div>
  );
};

export default PoliticianDetailPage;