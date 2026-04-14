import { Menubar } from 'primereact/menubar';

export default function BasicDemo() {
    const items = [
        {
            label: 'Home',
            icon: 'pi pi-home',
            command: () => {
                window.location.href = '/';
            }
        },
        {
            label: 'Account',
            icon: 'pi pi-user',
            items: [
                {
                    label: 'Logout',
                    icon: 'pi pi-times'
                }
            ]
        },
        {
            label: 'Support',
            icon: 'pi pi-envelope'
        }
    ];


    return (
        <div className="card">
            <Menubar 
                model={items}
            />
        </div>
    )
}
        