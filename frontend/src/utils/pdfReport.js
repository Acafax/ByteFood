/**
 * Opens a new browser tab with a printable inventory report.
 *
 * @param {Array<{name: string, quantity: number, unit: string}>} inventory
 */
export function generateInventoryReport(inventory) {
  const currentDate = new Date().toLocaleDateString('pl-PL', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  const rows = inventory
    .map(
      (item) => `
        <tr>
          <td>${item.name}</td>
          <td></td>
          <td>${item.quantity} ${item.unit}</td>
        </tr>`,
    )
    .join('');

  const htmlContent = `<!DOCTYPE html>
<html>
<head>
  <title>Raport Inwentaryzacyjny</title>
  <meta charset="UTF-8">
  <style>
    @page { size: A4; margin: 2cm; }
    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
    h1 { text-align: center; color: #333; margin-bottom: 10px; }
    .date { text-align: center; color: #666; margin-bottom: 30px; }
    table { width: 100%; border-collapse: collapse; margin-bottom: 60px; }
    th, td { border: 1px solid #333; padding: 10px; text-align: left; }
    th { background-color: #4F46E5; color: white; font-weight: bold; }
    tr:nth-child(even) { background-color: #f9f9f9; }
    .signature-section { margin-top: 80px; display: flex; justify-content: space-between; align-items: flex-end; }
    .signature-box { width: 250px; text-align: center; }
    .signature-line { border-top: 2px solid #333; margin-top: 60px; padding-top: 5px; }
    @media print { body { padding: 0; } button { display: none; } }
  </style>
</head>
<body>
  <h1>RAPORT INWENTARYZACYJNY</h1>
  <p class="date">Data raportu: ${currentDate}</p>

  <table>
    <thead>
      <tr>
        <th>Nazwa produktu</th>
        <th>Stan aktualny</th>
        <th>Stan docelowy</th>
      </tr>
    </thead>
    <tbody>${rows}</tbody>
  </table>

  <div class="signature-section">
    <div class="signature-box">
      <div class="signature-line">Podpis kierownika zmiany</div>
    </div>
  </div>

  <script>
    window.onload = function() { setTimeout(() => { window.print(); }, 250); };
  </script>
</body>
</html>`;

  const printWindow = window.open('', '_blank');
  printWindow.document.write(htmlContent);
  printWindow.document.close();
}
