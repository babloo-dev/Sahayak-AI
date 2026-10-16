export const roleOptions = [
  { value: 'citizen', label: 'Citizen / Reporter' },
  { value: 'rescue', label: 'Rescue Team' },
  { value: 'admin', label: 'Admin / Control Center' },
]

export const roleNavigation = {
  citizen: {
    label: 'Citizen / Reporter',
    items: [
      {
        id: 'citizen-dashboard',
        label: 'Dashboard',
        title: 'Citizen Dashboard',
        description: 'Your reporting and public safety workspace.',
        placeholder:
          'Your report summary and response progress will appear here when the backend is connected.',
      },
      {
        id: 'report-incident',
        label: 'Report an Incident',
        title: 'Report an Incident',
        description: 'Share incident details with the response team.',
        placeholder:
          'The incident form and secure submission flow are not connected yet.',
      },
      {
        id: 'my-reports',
        label: 'My Reports',
        title: 'My Reports',
        description: 'Track only the reports submitted by you and their rescue status.',
        placeholder:
          'Your reports and current rescue stages will appear here after citizen-specific API access is implemented.',
      },
      {
        id: 'early-warning',
        label: 'Early Warning',
        title: 'Early Warning / Predict Disaster Risk',
        description:
          'Planned: select a supported hazard and location; connected sources should show freshness and forecast horizon, with AI estimates kept separate from official alerts.',
        placeholder:
          'This planned feature is not connected. No risk estimate, forecast, or official alert data is being shown.',
        status: 'Planned · not connected',
        tone: 'info',
      },
      {
        id: 'official-alerts',
        label: 'Official Alerts',
        title: 'Official Alerts',
        description: 'View verified alerts separately from Sahayak AI estimates.',
        placeholder:
          'Official alert sources have not been connected. No live alerts are displayed.',
      },
    ],
  },
  rescue: {
    label: 'Rescue Team',
    items: [
      {
        id: 'rescue-dashboard',
        label: 'Dashboard',
        title: 'Rescue Team Dashboard',
        description: 'Your authorized assignments and response work.',
        placeholder:
          'Team assignments and response summaries will appear here when authorized backend access is implemented.',
      },
      {
        id: 'assigned-incidents',
        label: 'Assigned Incidents',
        title: 'Assigned Incidents',
        description: 'Review incidents assigned to your rescue team.',
        placeholder:
          'Assigned incident records are not connected. No sample or live incidents are shown.',
      },
      {
        id: 'response-updates',
        label: 'Response Updates',
        title: 'Update Response Status',
        description: 'Record progress for an authorized assignment.',
        placeholder:
          'Status updates and response history will be available after the authorized API workflow is implemented.',
      },
    ],
  },
  admin: {
    label: 'Admin / Control Center',
    items: [
      {
        id: 'control-center',
        label: 'Overview',
        title: 'Control Center',
        description: 'Operational workspace for incident coordination.',
        placeholder:
          'Operational summaries will be based on database records after the admin API is connected.',
      },
      {
        id: 'incident-management',
        label: 'Incident Management',
        title: 'Incident Management',
        description: 'Review incidents and coordinate response assignments.',
        placeholder:
          'Incident records, details, and assignment controls are not connected.',
      },
      {
        id: 'team-management',
        label: 'Rescue Teams',
        title: 'Rescue Team Management',
        description: 'Review eligible teams and their authorized work.',
        placeholder:
          'Rescue-team records and assignment eligibility are not connected.',
      },
      {
        id: 'resource-management',
        label: 'Resources',
        title: 'Resource Management',
        description: 'Coordinate response resources from stored records.',
        placeholder:
          'Resource inventory and allocation are not connected.',
      },
      {
        id: 'risk-assessments',
        label: 'Risk Assessments',
        title: 'Risk Assessments',
        description: 'Review risk information with its source and limitations.',
        placeholder:
          'Risk assessments will appear only when supported data or a validated model is available and connected.',
      },
      {
        id: 'analytics',
        label: 'Analytics',
        title: 'Analytics',
        description: 'Review operational analytics derived from stored records.',
        placeholder:
          'Analytics are not connected. No sample statistics are displayed.',
      },
    ],
  },
}
