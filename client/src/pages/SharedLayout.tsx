import Navbar from "../components/Navbar";
import Dashboard from "../components/Dashboard";

const SharedLayout = () => {
  return (
    <>
      <main className="dashboard">
        <Navbar />
        <section className="dashboard-page">
          <Dashboard />
        </section>
      </main>
    </>
  );
};

export default SharedLayout;
